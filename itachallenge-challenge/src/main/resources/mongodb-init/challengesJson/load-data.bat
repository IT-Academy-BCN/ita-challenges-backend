@echo off
echo Beginning of insertion

mongoimport --db=challenges --jsonArray --file=challenges.json
pause
mongoimport --db=challenges --jsonArray --file=languages.json
pause
mongoimport --db=challenges --jsonArray --file=solutions.json
echo Made it
pause
exit
