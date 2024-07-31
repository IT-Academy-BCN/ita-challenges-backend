package com.itachallenge.score.filter;


public class EscapeJava {
    public static String escapeJavaCode(String value) {
        return value
                .replace("\\", "\\\\") // Escapa las barras invertidas
                .replace("\"", "\\\"") // Escapa las comillas dobles
                .replace("'", "\\'") // Escapa las comillas simples
                .replace("\n", "\\n") // Escapa los saltos de línea
                .replace("\r", "\\r") // Escapa los retornos de carro
                .replace("\t", "\\t") // Escapa las tabulaciones
                .replace("\f", "\\f") // Escapa las form feeds
                .replace("<", "\\u003C") // Escapa los signos de menor que
                .replace(">", "\\u003E") // Escapa los signos de mayor que
                .replace("&", "\\u0026"); // Escapa los ampersands
    }
}
