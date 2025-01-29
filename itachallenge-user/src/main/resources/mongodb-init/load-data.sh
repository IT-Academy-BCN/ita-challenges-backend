#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=users --username admin_user --authenticationDatabase admin --password yxRG4sYBDjPFzbh5 --collection=mentors --jsonArray --file=mentors.json

echo "Creating unique index in mentors collection"
mongosh --eval "db.getSiblingDB('users').mentors.createIndex({ user_id: 1 }, { unique: true })"

echo "Done"
exit