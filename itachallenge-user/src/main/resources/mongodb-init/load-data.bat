@echo off
echo Beginning of insertion

mongoimport --db=user --collection=users --jsonArray --file=user.json
mongoimport --db=user --collection=user_challenge --jsonArray --file=user_challenge.json


echo "Creating unique index in User_Challenge collection"
mongosh --eval "db.getSiblingDB('user').user_challenge.createIndex({ user_id: 1, challenge_id: 1 }, { unique: true })"

echo Made it
exit