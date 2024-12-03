import java.nio.file.Paths;

public class NioFileExample {
    public static void main(String[] args) {
        try {
            Paths.get("test.txt");  // Esto utiliza el módulo java.nio
            System.out.println("File path created successfully");
        } catch (Exception e) {
            System.out.println("Expected failure: " + e.getMessage());
        }
    }
}