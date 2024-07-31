package com.itachallenge.score.filter;

public class EscapeFilter implements Filter {

    private Filter next;

    @Override
    public boolean apply(String input) {

        String escape = EscapeJava.escapeJavaCode(input);
        if (next != null) {
            return next.apply(escape);
        }

        return true;
    }


    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
