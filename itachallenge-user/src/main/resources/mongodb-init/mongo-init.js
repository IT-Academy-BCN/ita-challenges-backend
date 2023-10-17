//create user logged as root User
//use admin
db.createUser({
    user: "admin_user",
    pwd: "yxRG4sYBDjPFzbh5",
    roles: [
      { role: "dbOwner", db: "users" }
    ]
  });

//use users
//create collections
//db.createCollection("solutions");