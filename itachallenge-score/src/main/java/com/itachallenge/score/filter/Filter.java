package com.itachallenge.score.filter;

public interface Filter {

    boolean apply(String input);

    void setNext(Filter next);
}
