//create user logged as root User
/*
db.createUser({
    user: "admin_users",
    pwd: "BYBcMJEEWw5egRUo",
    roles: [
      { role: "dbUsersOwner", db: "users" }
    ]
  });
*/

//create collections

db.createCollection("user_challenge")
db.createCollection("user");