#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=challenges-users --username admin_challenges_user --authenticationDatabase admin --password BYBcMJEEWw5egRUo --collection=users --jsonArray --file=user_score.json

echo "Creating unique index in user_score collection"
mongosh --eval "db.getSiblingDB('user').user_score.createIndex({ user_id: 1, challenge_id: 1 }, { unique: true })"

echo "Done"
exit