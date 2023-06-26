@echo off
echo Beginning of insertion

echo "Inserting first object..."
mongosh user --eval "db.user_challenge.insertOne({'_id': '2a9bda26-10f2-11ee-be56-0242ac120002', user_id: '2dcacb291-b4aa-4029-8e9b-284c8ca80296', challenge_id: 'dcacb291-b4aa-4029-8e9b-284c8ca80296' })"


IF NOT ERRORLEVEL 1 (
  echo "First object inserted successfully."
) ELSE (
  echo "Error occurred while inserting the first object."
)

echo "Inserting duplicate object..."
mongosh user --eval "db.user_challenge.insertOne({ '_id': { '$UUID': '3a9bda26-10f2-11ee-be56-0242ac120002' }, user_id: '2dcacb291-b4aa-4029-8e9b-284c8ca80296', challenge_id: 'dcacb291-b4aa-4029-8e9b-284c8ca80296' })"

IF ERRORLEVEL 1 (
  echo "Duplicate object not inserted. Unique index is working."
) ELSE (
  echo "Error occurred. Unique index might not be working."
)

echo Made it
pause
exit