package com.itachallenge.challenge.config.dbchangelog;

import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChangeUnit(id="DatabaseUpdaterDemo", order = "2", author = "Ernesto Arcos")
public class DatabaseUpdater {
    private final Logger logger = LoggerFactory.getLogger(DatabaseUpdater.class);

    @Execution
    public void execution(){
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("UpdaterExecution");

    }

    @RollbackExecution
    public void rollBackExecution (){
        logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        logger.info("UpdaterRollbackExecution");
    }
}
