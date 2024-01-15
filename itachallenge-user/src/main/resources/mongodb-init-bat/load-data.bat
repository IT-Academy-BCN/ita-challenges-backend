@echo off
echo Beginning of insertion

mongoimport --db=users --collection=solutions --jsonArray --file=user_score.json

echo "Creating unique index in users.solutions collection"
mongosh --eval "db.getSiblingDB('users').solutions.createIndex({ user_id: 1, challenge_id: 1 }, { unique: true })"

echo Done
exit