#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=users --username admin_user --authenticationDatabase admin --password yxRG4sYBDjPFzbh5 --collection=users --jsonArray --file=users.json

echo "Creating unique index in users collection"
mongosh --eval "db.getSiblingDB('users').users.createIndex({ user_id: 1 }, { unique: true })"

echo "Done"
exit