#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=user --collection=user --jsonArray --file=users.json
mongoimport --db=user --collection=challenge --jsonArray --file=userchallenge.json

echo "Creating unique index in User_Challenge collection"
mongo user --eval "db.user_challenge.createIndex({ userId: 1, challengeId: 1 }, { unique: true })"

echo "Made it"
exit
