#!/bin/bash

echo "Loading data to mongoDB"

mongoimport --db=challenges  --username admin_challenge --authenticationDatabase admin --password BYBcMJEEWw5egRUo --collection=challenges --jsonArray --file=/tmp/data/challenges.json
mongoimport --db=challenges  --username admin_challenge --authenticationDatabase admin --password BYBcMJEEWw5egRUo --collection=languages --jsonArray --file=/tmp/data/languages.json
mongoimport --db=challenges  --username admin_challenge --authenticationDatabase admin --password BYBcMJEEWw5egRUo --collection=solutions --jsonArray --file=/tmp/data/solutions.json

