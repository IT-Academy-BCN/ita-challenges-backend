#!/bin/bash

echo "Loading data to mongoDB"

mongoimport --db=challenges  --username admin_challenge --authenticationDatabase challenges --password BYBcMJEEWw5egRUo --collection=challenges --jsonArray --file=C:\Users\TeH_h\Documents\GitHub\ita-challenges-backend\itachallenge-challenge\src\main\resources\mongodb-test-data\challenges.json
mongoimport --db=challenges  --username admin_challenge --authenticationDatabase challenges --password BYBcMJEEWw5egRUo --collection=languages --jsonArray --file=C:\Users\TeH_h\Documents\GitHub\ita-challenges-backend\itachallenge-challenge\src\main\resources\mongodb-test-dat\languages.json
mongoimport --db=challenges  --username admin_challenge --authenticationDatabase challenges --password BYBcMJEEWw5egRUo --collection=solutions --jsonArray --file=C:\Users\TeH_h\Documents\GitHub\ita-challenges-backend\itachallenge-challenge\src\main\resources\mongodb-test-data\solutions.json

