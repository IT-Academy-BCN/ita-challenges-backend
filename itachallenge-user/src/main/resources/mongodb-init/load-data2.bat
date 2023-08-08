@echo off
echo Beginning of insertion (comienza la importacion de datos)

mongoimport --db=challenges --collection=user_score2 --jsonArray --file=user_score2.json

echo "Creating unique index in user_score collection"
mongosh --eval "db.getSiblingDB('user').user_score2.createIndex({ user_id: 1, challenge_id: 1 }, { unique: true })"

echo Done
exit