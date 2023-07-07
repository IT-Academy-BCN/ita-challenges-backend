#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=user --collection=user_score --jsonArray --file=user_score.json

echo "Creating unique index in user_score collection"
mongosh --eval "db.getSiblingDB('user').user_score.createIndex({ user_id: 1, challenge_id: 1 }, { unique: true })"

echo "Made it"
exit