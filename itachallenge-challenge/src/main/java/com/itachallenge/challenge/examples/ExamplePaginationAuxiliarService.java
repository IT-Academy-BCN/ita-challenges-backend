package com.itachallenge.challenge.examples;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/*
Podría ser un helper / ser un servicio auxiliar, para
poder ser reusado por cualquier endpoint/micro que requiera
paginación.
 */
public class ExamplePaginationAuxiliarService {

    /*
    Conceptos paginación (una vez LA ORDENACIÓN ESTÉ HECHA):

    Offset = número de retos a excluir (una vez ordenados)
    Limit = máximo número de retos a incluir en la respuesta
    Size = tamaño de la página = Limit
    Page = Offset/Size = Offset/Limit <-> Offset = page * size
    <-> Primera página (page) tiene como valor 0.
     */

    private int offset;

    private int limit;

    //
    public ExamplePaginationAuxiliarService(int offset, int limit) {
        //assert offset >= 0; //valores validos proporcionados por el FE
        //assert limit >= -1; //valores validos proporcionados por el FE
        this.offset = offset;
        this.limit = limit;
    }

    /*
    método para seleccionar los que queramos de un flujo X
     */
    public Flux<Object> doPagination(Flux<Object> fluxSorted){
        return doLimit(doOffset(fluxSorted));
    }

    private Flux<Object> doOffset(Flux<Object> fluxSorted){
        if(offset > 0) {
            return fluxSorted.skip(offset);
        }else {
            return fluxSorted;
        }
    }

    private Flux<Object> doLimit(Flux<Object> fluxOffsetted){
        if(limit >= 0) {
            return fluxOffsetted.take(limit);
        }else {
            return fluxOffsetted;
        }
    }

    /*
    también se podria tener un método que generara directamente la PageDto
     */

    public Mono<PageDto> createPage(Flux<Object> fluxSorted){
        return doPagination(fluxSorted) //Flux<Object> con solo los elementos a mostrat
                .collectList() //Mono<List<Object>>
                .map(items -> new PageDto(offset,limit,items));
    }


}

class PageDto { //misma info que el GenericResultsDto<T> del BussinessAssistant project

    private int offset;

    private int limit;

    private List<Object> results; //items, pero jonatan "le gusta este nombre" jejej

    public PageDto(int offset, int limit, List<Object> results) {
        this.offset = offset;
        this.limit = limit;
        this.results = results;
    }

    public int getCount(){  //atributo count tiene que estar en el dto serializado
        return results.size();
    }
}
