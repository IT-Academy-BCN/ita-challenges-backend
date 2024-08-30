package com.itachallenge.score.sandBox.sandBox_container;

import java.util.List;

public class CustomClassLoader extends ClassLoader {
    private List<String> prohibitedClasses;

    public CustomClassLoader(ClassLoader parent, List<String> prohibitedClasses) {
        super(parent);
        this.prohibitedClasses = prohibitedClasses;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        prohibitedClasses = getProhibitedClasses();

        if (prohibitedClasses.contains(name)) {
            throw new ClassNotFoundException("Loading of class " + name + " is prohibited");
        }
        return super.loadClass(name);
    }

    public List<String> getProhibitedClasses() throws ClassNotFoundException {
        return JavaSandboxContainer.runWithCustomClassLoader();
    }
}
