//DB challenges
//use challenges

db.createUser(
  {
    user: "adminChallenge",
    pwd: "01TZFSLwZ0",
    roles: [
      { role: "dbOwner", db: "challenges" }
    ]
  }
)




