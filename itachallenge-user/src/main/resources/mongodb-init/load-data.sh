#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=users --username admin_user --authenticationDatabase admin --password yxRG4sYBDjPFzbh5 --collection=solutions --jsonArray --file=user_score.json

echo "Creating unique index in solutions collection"
mongosh --eval "db.getSiblingDB('users').solutions.createIndex({ user_id: 1, challenge_id: 1 }, { unique: true })"

echo "Done"
exit