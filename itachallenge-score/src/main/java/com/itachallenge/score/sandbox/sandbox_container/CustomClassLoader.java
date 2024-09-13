package com.itachallenge.score.sandbox.sandbox_container;

import java.util.List;

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

    public List<String> getProhibitedClasses() {
        return prohibitedClasses;
    }
}