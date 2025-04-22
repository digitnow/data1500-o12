
db = db.getSiblingDB('twitter');
print("Twitter database ready");



// docker exec data1500-o12-mongo-1 mongoimport --db twitter --collection tweets --file /data/tweets.json --type json --jsonArray