package com.itachallenge.score.filter;

import com.itachallenge.score.util.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("securityFilter")
@ConfigurationProperties(prefix = "security")
public class SecurityFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class.getName());
    private List<String> harmfulPatterns = new ArrayList<>();

    public List<String> getHarmfulPatterns() {
        return harmfulPatterns;
    }

public void setHarmfulPatterns(List<String> harmfulPatterns) {
    if (harmfulPatterns != null && !harmfulPatterns.isEmpty()) {
        this.harmfulPatterns = getHarmfulPatterns();
        log.info("Harmful patterns set: {}", harmfulPatterns);
    }
}


        @Override
        public ExecutionResult apply (String sourceCode){
            ExecutionResult result = new ExecutionResult();

            for (String pattern : harmfulPatterns) {
                if (sourceCode.contains(pattern)) {
                    log.info("Security violation detected: {} \n Aborting execut", pattern);
                    result.setSuccess(false);
                    result.setMessage("Security violation: potentially harmful code detected: " + pattern);

                    return result;
                }
            }

            result.setSuccess(true);
            result.setMessage("Code passed security filter");
            log.info("All filters passed. Code is safe to execute.");

            return result;
        }

        @Override
        public void setNext (Filter next){
            // No next filter
        }
    }