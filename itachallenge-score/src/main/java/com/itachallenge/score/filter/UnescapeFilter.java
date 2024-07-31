package com.itachallenge.score.filter;

public class UnescapeFilter implements Filter {

    private Filter next;

    @Override
    public boolean apply(String input) {

        String code = UnescapeJava.unescapeJavaCode(input);
        if (next != null) {
            return next.apply(code);
        }

        return true;
    }


    @Override
    public void setNext(Filter next) {
        this.next = next;
    }
}
