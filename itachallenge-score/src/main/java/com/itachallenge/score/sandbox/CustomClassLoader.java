package com.itachallenge.score.sandbox;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomClassLoader extends ClassLoader {
    private List<String> prohibitedClasses;

    public CustomClassLoader(ClassLoader parent, List<String> prohibitedClasses) {
        super(parent);
        this.prohibitedClasses = prohibitedClasses;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        for (String prohibitedClass : prohibitedClasses) {
            if (name.startsWith(prohibitedClass)) {
                throw new ClassNotFoundException("Class " + name + " is prohibited");
            }
        }
        return super.loadClass(name);
    }

    public static boolean isLibraryImportAllowed(String code) {
        List<String> forbiddenLibraries = JavaSandboxContainer.getProhibitedClasses();
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

    public List<String> getProhibitedClasses() {
        return prohibitedClasses;
    }
}