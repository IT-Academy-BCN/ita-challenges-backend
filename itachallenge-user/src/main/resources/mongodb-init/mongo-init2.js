//create user logged as root User;

db.createUser({
    user: "admin_challenges",
    pwd: "BYBcMJEEWw5egRUo",
    roles: [
      { role: "dbOwner", db: "challenges" }
    ]
  });
use challenges;
//create collections
db.createCollection("user_score2");