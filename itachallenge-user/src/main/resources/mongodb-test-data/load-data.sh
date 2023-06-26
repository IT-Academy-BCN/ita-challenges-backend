#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=users --collection=user --jsonArray --file=users.json
mongoimport --db=users --collection=challenge --jsonArray --file=user_challenge.json

echo "Creating unique index in User_Challenge collection"
mongo users --eval "db.user_challenge.createIndex({ userId: 1, challengeId: 1 }, { unique: true })"

echo "Made it"
exit