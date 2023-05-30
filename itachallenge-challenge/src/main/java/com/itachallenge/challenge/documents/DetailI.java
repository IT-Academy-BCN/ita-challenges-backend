package com.itachallenge.challenge.documents;

import java.util.List;

public interface DetailI {

    String getDescription();

    //TODO: preguntar jonatan si nos podemos cargar UUID example
    // + examples directamente en details
    //Opcion 1:
    List<ExampleI> getExamples();
    //Opcion 2:
    //List<String> getExample_texts(); // getExamples()

    String getNote();


}
