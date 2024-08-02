package com.itachallenge.score.filter;



public class UnescapeJava {

    private UnescapeJava() {
    }


    public static String unescapeJavaCode(String value) {
        return value
                .replace("\\u003C", "<") // Replace less than
                .replace("\\u003E", ">") // Replace greater than
                .replace("\\u0026", "&") // Replace ampersand
                .replace("\\f", "\f") // Replace form feed
                .replace("\\t", "\t") // Replace tab
                .replace("\\r", "\r") // Replace carriage return
                .replace("\\n", "\n") // Replace new line
                .replace("\\'", "'") // Replace single quote
                .replace("\\\"", "\"") // Replace double quote
                .replace("\\\\", "\\"); // Replace backslash
    }
}
