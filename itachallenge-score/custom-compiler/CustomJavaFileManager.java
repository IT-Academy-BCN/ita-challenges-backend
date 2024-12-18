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
        Set<String> allowedModules = Set.of("java.base", "java.compiler", "jdk.compiler");
        for (Module module : ModuleLayer.boot().modules()) {
            if (allowedModules.contains(module.getName())) {
                boolean isPackageAllowed = packageName == null || packageName.isEmpty() ||
                        (module.isNamed() && module.getPackages().contains(packageName)) ||
                        (module.getName().equals("java.base") && packageName.equals("java"));
                if (isPackageAllowed) {
                    return super.list(location, packageName, kinds, recurse);
                }
            }
        }
        throw new SecurityException("Access to package " + packageName + " is restricted");
    }
}