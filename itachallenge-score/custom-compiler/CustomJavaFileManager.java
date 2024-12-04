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
        // Restrict access to certain packages
        if (packageName.isEmpty() || packageName.startsWith("java.base") || packageName.startsWith("java.compile") || packageName.startsWith("jdk.compile") || packageName.startsWith("java.lang") || packageName.startsWith("java.io") || packageName.startsWith("java.math") || packageName.startsWith("java.net") || packageName.startsWith("java.nio") || packageName.startsWith("java.security") || packageName.startsWith("java.text") || packageName.startsWith("java.time") || packageName.startsWith("java.util")) {
            return super.list(location, packageName, kinds, recurse);
        }
        throw new SecurityException("Access to package " + packageName + " is restricted");
    }
}