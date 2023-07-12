//create user logged as root User
db.createUser({
    user: "admin_user",
    pwd: "BYBcMJEEWw5egRUo",
    roles: [
      { role: "dbUsersOwner", db: "user" }
    ]
  });

//create collections
db.createCollection("users");