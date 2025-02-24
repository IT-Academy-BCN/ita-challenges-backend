@echo off
echo Beginning of insertion

mongoimport --db=users --collection=mentors --jsonArray --file=users.json

echo "Creating unique index in users.users collection"
mongosh --eval "db.getSiblingDB('users').users.createIndex({ user_id: 1}, { unique: true })"

echo Done
exit