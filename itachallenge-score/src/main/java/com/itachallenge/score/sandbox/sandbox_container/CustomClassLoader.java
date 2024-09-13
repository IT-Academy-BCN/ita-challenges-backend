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
        if (prohibitedClasses.contains(name)) {
            throw new ClassNotFoundException("Loading of class " + name + " is prohibited");
        }
        return super.loadClass(name);
    }

    public List<String> getProhibitedClasses() {
        return prohibitedClasses;
    }
}