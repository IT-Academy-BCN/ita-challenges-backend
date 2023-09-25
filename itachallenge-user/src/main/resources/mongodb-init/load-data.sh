#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=users --username admin_challenges_user --authenticationDatabase admin --password BYBcMJEEWw5egRUo --collection=challenges --jsonArray --file=user_score.json

echo "Creating unique index in challenges collection"
mongosh --eval "db.getSiblingDB('users').challenges.createIndex({ user_id: 1, challenge_id: 1 }, { unique: true })"

echo "Done"
exit