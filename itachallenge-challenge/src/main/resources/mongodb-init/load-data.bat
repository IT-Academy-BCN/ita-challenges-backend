@echo off
echo Beginning of insertion

mongoimport --db=challenges --jsonArray --file=challenges.json
mongoimport --db=challenges --jsonArray --file=languages.json
mongoimport --db=challenges --jsonArray --file=solutions.json
echo Made it
exit
