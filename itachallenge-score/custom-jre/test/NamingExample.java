import javax.naming.InitialContext;
import javax.naming.NamingException;

public class NamingExample {
    public static void main(String[] args) {
        try {
            InitialContext ctx = new InitialContext();
            System.out.println("Naming module test");
        } catch (NamingException e) {
            System.out.println("Expected failure: " + e.getMessage());
        }
    }
}


/* Expected output:
**
** Error: Unable to initialize main class NamingExample
** Caused by: java.lang.NoClassDefFoundError: javax/naming/NamingException */