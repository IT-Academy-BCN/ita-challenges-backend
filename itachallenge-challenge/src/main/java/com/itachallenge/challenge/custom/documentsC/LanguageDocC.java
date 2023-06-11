package com.itachallenge.challenge.custom.documentsC;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "languages")
public class LanguageDocC {

    //TODO: ¿¿¿ eliminar el _class del BSON ???

    /*
    Recommendations from official docs:
    https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mapping.conventions.id-field

    @MongoId:  it will be converted to and stored as using its actual type
        (If no value is provided -> a new ObjectId will be created and converted to the properties type.)
        *** Aquesta anotació NO permet la conjunció amb @Field (per a modificar el nom del camp)

    @Id: suporta qualsevol tipus primitiu
     */
    @Id
    //TODO: advertir que la unica manera (en docs) que un BSON tingui el camp ID amb un altre nom
    // que no sigui el '_id:' es amb @Id + @Field("xxx") + nom atribut "id"
    private Integer languageId;

    /*
    TODO: constraint for unique + not null
     */
    @Field(name="language_name")
    private String language;

    /*
    Implementar relación respecto los retos en caso de necessidad
     */

    /*
    Recommendation from official docs:
    https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mapping.object-creation
    https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mapping.property-population
    https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/#mapping.general-recommendations

    One all args constructor. Inf not needed in domain -> package visibility
        *Lombock @AllArgsContructor -> public
     */
    public LanguageDocC(int languageId, String language) {
        this.languageId = languageId;
        this.language = language;
    }
}
