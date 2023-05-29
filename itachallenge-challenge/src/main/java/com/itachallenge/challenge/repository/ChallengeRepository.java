package com.itachallenge.challenge.repository;

import com.itachallenge.challenge.documents.Challenge;
import org.reactivestreams.Publisher;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Repository
public class ChallengeRepository implements ReactiveMongoRepository<Challenge, String> {

    @Override
    public <S extends Challenge> Mono<S> insert(S entity) {
        return null;
    }

    @Override
    public <S extends Challenge> Flux<S> insert(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends Challenge> Flux<S> insert(Publisher<S> entities) {
        return null;
    }

    @Override
    public <S extends Challenge> Mono<S> findOne(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Challenge> Flux<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Challenge> Flux<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Challenge> Mono<Long> count(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Challenge> Mono<Boolean> exists(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Challenge, R, P extends Publisher<R>> P findBy(Example<S> example, Function<FluentQuery.ReactiveFluentQuery<S>, P> queryFunction) {
        return null;
    }

    @Override
    public <S extends Challenge> Mono<S> save(S entity) {
        return null;
    }

    @Override
    public <S extends Challenge> Flux<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public <S extends Challenge> Flux<S> saveAll(Publisher<S> entityStream) {
        return null;
    }

    @Override
    public Mono<Challenge> findById(String s) {
        return null;
    }

    @Override
    public Mono<Challenge> findById(Publisher<String> id) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(String s) {
        return null;
    }

    @Override
    public Mono<Boolean> existsById(Publisher<String> id) {
        return null;
    }

    @Override
    public Flux<Challenge> findAll() {
        return null;
    }

    @Override
    public Flux<Challenge> findAllById(Iterable<String> strings) {
        return null;
    }

    @Override
    public Flux<Challenge> findAllById(Publisher<String> idStream) {
        return null;
    }

    @Override
    public Mono<Long> count() {
        return null;
    }

    @Override
    public Mono<Void> deleteById(String s) {
        return null;
    }

    @Override
    public Mono<Void> deleteById(Publisher<String> id) {
        return null;
    }

    @Override
    public Mono<Void> delete(Challenge entity) {
        return null;
    }

    @Override
    public Mono<Void> deleteAllById(Iterable<? extends String> strings) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Iterable<? extends Challenge> entities) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll(Publisher<? extends Challenge> entityStream) {
        return null;
    }

    @Override
    public Mono<Void> deleteAll() {
        return null;
    }

    @Override
    public Flux<Challenge> findAll(Sort sort) {
        return null;
    }
}
