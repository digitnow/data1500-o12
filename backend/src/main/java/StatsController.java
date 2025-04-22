// StatsController.java
import com.mongodb.client.*;
import org.bson.Document;
import java.util.*;
import java.text.SimpleDateFormat;

public class StatsController {
    private final MongoDatabase db;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public StatsController(MongoClient mongoClient) {
        this.db = mongoClient.getDatabase("twitter");
    }

    public Document getStats() {
        Document stats = new Document();
        stats.append("activity", getActivityStats());
        stats.append("popularity", getPopularityStats());
        return stats;
    }

    private Document getActivityStats() {
        List<Document> pipeline = Arrays.asList(
            new Document("$addFields", 
                new Document("convertedDate", 
                    new Document("$dateFromString", 
                        new Document("dateString", "$Timestamp")
                            .append("format", "%Y-%m-%d %H:%M:%S")
                    )
                )
            ),
            new Document("$group", 
                new Document("_id", 
                    new Document("$dateToString", 
                        new Document("format", "%Y-%m-%d")
                            .append("date", "$convertedDate")
                    )
                )
                .append("count", new Document("$sum", 1))
            ),
            new Document("$sort", new Document("_id", 1))
        );

        List<String> dates = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();

        db.getCollection("tweets")
            .aggregate(pipeline)
            .forEach(doc -> {
                dates.add(doc.getString("_id"));
                counts.add(doc.getInteger("count"));
            });

        return new Document("dates", dates)
            .append("counts", counts);
    }

    private Document getPopularityStats() {
        List<Document> pipeline = Arrays.asList(
            new Document("$addFields", 
                new Document("convertedLikes", 
                    new Document("$toInt", "$Likes")
                )
            ),
            new Document("$group",
                new Document("_id", "$Username")
                    .append("totalLikes", new Document("$sum", "$convertedLikes"))
            ),
            new Document("$sort", new Document("totalLikes", -1)),
            new Document("$limit", 5)
        );

        List<String> usernames = new ArrayList<>();
        List<Integer> likes = new ArrayList<>();

        db.getCollection("tweets")
            .aggregate(pipeline)
            .forEach(doc -> {
                usernames.add(doc.getString("_id"));
                likes.add(doc.getInteger("totalLikes"));
            });

        return new Document("usernames", usernames)
            .append("likes", likes);
    }
}