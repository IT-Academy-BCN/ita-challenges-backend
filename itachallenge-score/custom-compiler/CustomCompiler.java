import javax.tools.*;
import java.io.File;
import java.util.Arrays;

public class CustomCompiler {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Please provide the path to the Java file to compile.");
            return;
        }

        String sourceFilePath = args[0];

        // Get the JavaCompiler instance
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // Get the standard file manager
        StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(null, null, null);

        // Create the custom file manager
        CustomJavaFileManager customFileManager = new CustomJavaFileManager(standardFileManager);

        // Specify the Java source file to compile
        Iterable<? extends JavaFileObject> compilationUnits = customFileManager.getJavaFileObjectsFromFiles(
                Arrays.asList(new File(sourceFilePath))
        );

        // Compile the source file
        JavaCompiler.CompilationTask task = compiler.getTask(null, customFileManager, null, null, null, compilationUnits);
        boolean success = task.call();

        if (success) {
            System.out.println("Compilation successful");
        } else {
            System.out.println("Compilation failed");
        }
    }
}