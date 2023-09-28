#!/bin/bash

echo "Beginning of insertion"

#mongoimport --db=challenges  --username admin_challenge --password BYBcMJEEWw5egRUo --collection=challenges --jsonArray --file=challenges.json
#mongoimport --db=challenges  --username admin_challenge --password BYBcMJEEWw5egRUo --collection=languages --jsonArray --file=languages.json
mongoimport --db=challenges  --username admin_challenge --password BYBcMJEEWw5egRUo --collection=solutions --jsonArray --file=solutions.json

echo "Done!"
exit
