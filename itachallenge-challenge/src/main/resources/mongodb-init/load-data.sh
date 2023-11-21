#!/bin/bash

echo "Loading data to mongoDB"

mongoimport --db=challenges --username admin_challenge --authenticationDatabase admin --password BYBcMJEEWw5egRUo --collection=challenges --jsonArray --file="$(pwd)/challenges.json"
mongoimport --db=challenges --username admin_challenge --authenticationDatabase admin --password BYBcMJEEWw5egRUo --collection=languages --jsonArray --file="$(pwd)/languages.json"
mongoimport --db=challenges --username admin_challenge --authenticationDatabase admin --password BYBcMJEEWw5egRUo --collection=solutions --jsonArray --file="$(pwd)/solutions.json"
