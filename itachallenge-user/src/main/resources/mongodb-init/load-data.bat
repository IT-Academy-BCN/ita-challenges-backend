@echo off
echo Beginning of insertion

mongoimport --db=users --collection=users --jsonArray --file=users.json

echo Made it
exit