import javax.tools.*;
import java.io.IOException;
import java.util.List;

public class CustomJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    protected CustomJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    @Override
    public JavaCompiler getJavaCompiler() {
        // Return a JavaCompiler instance that uses the custom JRE
        return ToolProvider.getSystemJavaCompiler();
    }

    // Override other methods as needed
}