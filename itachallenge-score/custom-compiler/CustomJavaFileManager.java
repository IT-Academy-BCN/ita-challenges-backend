import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class CustomJavaFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    public CustomJavaFileManager(StandardJavaFileManager standardFileManager) {
        super(standardFileManager);
    }

    public Iterable<? extends JavaFileObject> getJavaFileObjectsFromFiles(List<File> files) {
        return fileManager.getJavaFileObjectsFromFiles(files);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        // Customize the file output location or behavior if needed
        return super.getJavaFileForOutput(location, className, kind, sibling);
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        // Customize the class loader if needed
        return super.getClassLoader(location);
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        // Define the allowed modules
        Set<String> allowedModules = Set.of("java.base", "java.compiler", "jdk.compiler");

        // Iterate through all modules in the boot layer
        for (Module module : ModuleLayer.boot().modules()) {
            // Check if the module is in the allowed list
            if (allowedModules.contains(module.getName())) {
                // If the package name is empty, allow access based on the module
                if (packageName == null || packageName.isEmpty()) {
                    return super.list(location, packageName, kinds, recurse);
                }
                // Check if the package is in the module, only if the module is named
                if (module.isNamed() && module.getPackages().contains(packageName)) {
                    return super.list(location, packageName, kinds, recurse);
                }
            }
        }

        // If the module is not allowed or the package couldn't be found, restrict access
        throw new SecurityException("Access to package " + packageName + " is restricted");
    }
}