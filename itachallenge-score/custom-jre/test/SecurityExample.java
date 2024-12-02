import org.ietf.jgss.GSSManager;

public class SecurityExample {
    public static void main(String[] args) {
        GSSManager manager = GSSManager.getInstance();
        System.out.println("GSS-API module test");
    }
}

/* Expected output:
**
** Exception in thread "main" java.lang.NoClassDefFoundError: org/ietf/jgss/GSSManager
** at SecurityExample.main(SecurityExample.java:5)
** Caused by: java.lang.ClassNotFoundException: org.ietf.jgss.GSSManager
** at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(Unknown Source)
** at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(Unknown Source)
** at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
** ... 1 more */