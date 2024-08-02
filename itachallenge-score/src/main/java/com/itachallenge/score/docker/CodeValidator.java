package com.itachallenge.score.docker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CodeValidator {

    private CodeValidator() {
    }

    private static final String[] forbiddenLibraries = {
            "java\\.lang\\.System",
            "java\\.io\\.PrintStream",
            "java\\.io\\.File",
            "java\\.io\\.FileReader",
            "java\\.io\\.FileWriter",
            "java\\.io\\.BufferedReader",
            "java\\.io\\.BufferedWriter",
            // se puede agregar más librerías aquí
    };

    public static boolean isLibraryImportAllowed(String code) {
        for (String lib : forbiddenLibraries) {
            String importPattern = "import\\s+" + lib + ";";
            Pattern pattern = Pattern.compile(importPattern);
            Matcher matcher = pattern.matcher(code);
            if (matcher.find()) {
                return false; // Importación prohibida encontrada
            }
        }
        return true;
    }
}


