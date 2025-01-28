@echo off
echo Beginning of insertion

mongoimport --db=users --collection=mentors --jsonArray --file=mentors.json

echo "Creating unique index in users.mentors collection"
mongosh --eval "db.getSiblingDB('users').mentors.createIndex({ user_id: 1}, { unique: true })"

echo Done
exit