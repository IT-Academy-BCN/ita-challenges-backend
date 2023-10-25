//create user logged as root User
//use admin
db.createUser({
    user: "admin_challenge",
    pwd: "BYBcMJEEWw5egRUo",
    roles: [
      { role: "dbOwner", db: "challenges" }
    ]
  });


//use challenges
//create collections
//db.createCollection("challenges");
//db.createCollection("languages");
//db.createCollection("solutions");





