import javax.tools.SimpleJavaFileObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

public class OutputFileObject extends SimpleJavaFileObject {
    private final File file;

    public OutputFileObject(URI uri, Kind kind) {
        super(uri, kind);
        this.file = new File(uri);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        // Create directories if they don't exist
        file.getParentFile().mkdirs();
        return new FileOutputStream(file);
    }
}