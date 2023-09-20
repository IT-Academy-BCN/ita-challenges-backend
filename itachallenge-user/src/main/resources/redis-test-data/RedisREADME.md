
## **Redis**


### Use redis-cli to Execute the Script:
 * redis-cli -h localhost -p 6379 < usersRedis.txt

### **Important**
When you execute the redis-cli command to import the data, make sure you are in the same directory as the ### 'usersRedis' file or provide the full path to the file in the command:
 * cd /path/to/my_project/
 * redis-cli -h localhost -p 6379 < users

 Ensure that your Redis server is running and accessible on the specified host and port
 
For more info check [Import Data into Redis](https://developer.redis.com/guides/import/)
