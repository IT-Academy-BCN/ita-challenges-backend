public class ExternalModuleExample {
    public static void main(String[] args) {
        try {
            Class.forName("com.example.NonExistentClass");
        } catch (ClassNotFoundException e) {
            System.out.println("Expected failure: " + e.getMessage());
        }
    }
}

/* Expected output:
**
** Expected failure: com.example.NonExistentClass */