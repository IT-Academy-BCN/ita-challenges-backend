package com.itachallenge.user.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties
public class PropertiesConfig {
    //region PROPERTIES: Private
    @Value("${url.max_length}")
    private Integer maxLength;

    //endregion PROPERTIES: Private


    //region GETTERS
    public Integer getUrlMaxLength(){return maxLength;}

    //endregion GETTERS


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GetItem {
        private String type;
        private String description;
        public String getType() { return type; }
        public void setType(String type) {this.type = type;}
        public String getDescription(){return description;}
        public void setDescription(String description){this.description = description;}
    }

}
