import javax.tools.*;
import java.io.*;
import java.util.*;
import java.net.URI;
import java.lang.module.*;

public class CustomJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    public CustomJavaFileManager(StandardJavaFileManager standardFileManager) {
        super(standardFileManager);
    }

    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(List<File> files) {
        return fileManager.getJavaFileObjectsFromFiles(files);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        // Determine the directory of the .java file (sibling)
        File sourceFile = new File(sibling.toUri());
        File parentDirectory = sourceFile.getParentFile();

        if (parentDirectory == null) {
            throw new IOException("Unable to determine the directory for the source file: " + sibling.getName());
        }

        // Construct the path for the .class file in the same directory
        File classFile = new File(parentDirectory, className.substring(className.lastIndexOf('.') + 1) + kind.extension);

        return new OutputFileObject(classFile.toURI(), kind);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        // Define the allowed modules
        Set<String> allowedModules = Set.of("java.base", "java.compiler", "jdk.compiler");
        Set<String> allowedPackagesInBase = Set.of(
                "java.lang",
                "java.util",
                "java.io",
                "java.math",
                "java.text",
                "java.time",
                "java"
        );
        // Iterate through all modules in the boot layer
        for (Module module : ModuleLayer.boot().modules()) {
            // Check if the module is in the allowed list
            if (allowedModules.contains(module.getName())) {
                // If the package name is empty, allow access based on the module
                if (packageName == null || packageName.isEmpty()) {
                    return super.list(location, packageName, kinds, recurse);
                }
                // Allow all packages in the java.base module
                if (module.getName().equals("java.base")) {
                    if (allowedPackagesInBase.contains(packageName)) {
                        System.out.println("Accessing package " + packageName + " in module " + module.getName() + "1er Filtro");
                        return super.list(location, packageName, kinds, recurse);
                    }
                }
                // Check if the package is in the module, only if the module is named
                if (module.isNamed() && module.getPackages().contains(packageName)) {
                    System.out.println("Accessing package " + packageName + " in module " + module.getName() + "2do Filtro");
                    return super.list(location, packageName, kinds, recurse);
                }
            }
        }

        // If the module is not allowed or the package couldn't be found, restrict access
        throw new SecurityException("Access to package " + packageName + " is restricted");
    }
}