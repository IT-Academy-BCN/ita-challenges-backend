package com.itachallenge.user.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ErrorsDocument {

    @Field(name = "error_description")
    private String errorDescription;

}
