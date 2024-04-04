package com.itachallenge.challenge.config.dbchangelog;

import com.itachallenge.challenge.document.ChallengeDocument;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import static org.springframework.data.mongodb.core.query.Criteria.where;

//@ChangeUnit(id="FirstChangelog", order = "1", author = "Jonatan Vicente")
public class DatabaseChangelog {

    Logger logger = LoggerFactory.getLogger(DatabaseChangelog.class);
    AtomicInteger successfulUpdatesCounter = new AtomicInteger();

    @Execution
    public void setFirstAndLastNameToUsers(MongoTemplate mongoTemplate) {
        Query query = new Query(
                where("_id").ne(null)
/*                        .andOperator(where("firstName").is(null),
                                where("lastName").is(null))*/
        );

        query.fields().include("_id", "challenge_title");
        long countChallenges = mongoTemplate.count(query, ChallengeDocument.class);
        query.limit(100);

        List<ChallengeDocument> challenges = mongoTemplate.find(query, ChallengeDocument.class);


        if(challenges != null || challenges.size() != 0){
            challenges.forEach(challenge -> {
                try {
                    logger.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                    logger.info(challenge.getUuid() + " - " + challenge.getTitle());

/*                    Criteria criteria = where("_id").is(challenge.getUuid());
                    Update update = new Update()
                            .set("level", "EASY");
                            //.set("lastName", lastName);

                    mongoTemplate.findAndModify(new Query(criteria), update, ChallengeDocument.class);
                    successfulUserUpdatesCounter.getAndIncrement();*/
                } catch (Exception ex) {
                    logger.error(String.format("Faield to set firstName & lastName for user with id %s", challenge.getUuid()), ex);
                }
            });
        }
        logger.info("Updated {} challenges out of {} total.", successfulUpdatesCounter,  countChallenges);

    }

    @RollbackExecution
    public void rollback() {
        // Our change is backward-compatible; we don't need to implement a rollback mechanism.
    }
}

