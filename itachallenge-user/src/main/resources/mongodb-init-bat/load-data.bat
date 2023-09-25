@echo off
echo Beginning of insertion

mongoimport --db=users --collection=challenges --jsonArray --file=user_score.json

echo "Creating unique index in users.challenges collection"
mongosh --eval "db.getSiblingDB('users').challenges.createIndex({ user_id: 1, challenge_id: 1 }, { unique: true })"

echo Done
exit