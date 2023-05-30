package com.itachallenge.challenge.documents;

import java.util.UUID;

public interface SolutionI {

    UUID getId_solution(); // @MongoId
    String getSolution_text();

    //opcion A)
    //de momento la opción A parece suficiente (teniendo en cuenda que el valor también está en properties)
    int getId_language();

    //Opcion B)
    //LanguageI getLanguage(); //@DBRef

    //Challenge getChallenge(); //@DBRef como alternativa a lo indicado en Challenge
}
