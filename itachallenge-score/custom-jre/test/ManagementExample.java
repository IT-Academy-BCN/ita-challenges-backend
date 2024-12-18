import java.lang.management.ManagementFactory;

public class ManagementExample {
    public static void main(String[] args) {
        System.out.println("Available Processors: " +
                ManagementFactory.getOperatingSystemMXBean().getAvailableProcessors());
    }
}

/* Expected output:
**
** Exception in thread "main" java.lang.NoClassDefFoundError: java/lang/management/ManagementFactory
**         at ManagementExample.main(ManagementExample.java:6)
** Caused by: java.lang.ClassNotFoundException: java.lang.management.ManagementFactory
**         at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(Unknown Source)
**         at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(Unknown Source)
**         at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
**         ... 1 more */