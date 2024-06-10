# spring-batch5-mongodb

Demo project for spring batch + mongo db. 
In this project we read the sample data from mongo db, process it and write into csv file.

### Set up
1. start mongodb instance from docker-compose
2. make mongorestore of sample data from here - https://github.com/mcampo2/mongodb-sample-databases/tree/master/dump/sample_training

### Usage
1. start this app
2. call "/launchJob" endpoint to read the sample data from Mongodb, process it and write into csv file
