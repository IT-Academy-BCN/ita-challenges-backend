#!/bin/bash

echo "Beginning of insertion"

mongoimport --db=challenges --collection=challenges --jsonArray --file=challenges.json
mongoimport --db=challenges --collection=languages --jsonArray --file=languages.json
mongoimport --db=challenges --collection=solutions --jsonArray --file=solutions.json

echo "Made it"
exit
