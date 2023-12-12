#!/bin/bash

echo "Loading data to mongoDB"

mongoimport --db=challenges  --username admin_challenge --authenticationDatabase admin --password Q68TFr35eoheKUsC --collection=challenges --jsonArray --file=/tmp/data/challenges.json
mongoimport --db=challenges  --username admin_challenge --authenticationDatabase admin --password Q68TFr35eoheKUsC --collection=languages --jsonArray --file=/tmp/data/languages.json
mongoimport --db=challenges  --username admin_challenge --authenticationDatabase admin --password Q68TFr35eoheKUsC --collection=solutions --jsonArray --file=/tmp/data/solutions.json