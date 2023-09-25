//create user logged as root User
db.createUser({
    user: "admin_challenges_user",
    pwd: "BYBcMJEEWw5egRUo",
    roles: [
      { role: "dbOwner", db: "users" }
    ]
  });

//create collections
db.createCollection("challenges");