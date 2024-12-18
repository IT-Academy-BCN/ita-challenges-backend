import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class JMXExample {
    public static void main(String[] args) throws Exception {
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName("com.example:type=Hello");
        System.out.println("Platform MBean Server: " + mbs.getDefaultDomain());
    }
}

/* Expected output:
*
*  Exception in thread "main" java.lang.NoClassDefFoundError: java/lang/management/ManagementFactory
        at JMXExample.main(JMXExample.java:7)
Caused by: java.lang.ClassNotFoundException: java.lang.management.ManagementFactory
        at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(Unknown Source)
        at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(Unknown Source)
        at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
        ... 1 more*/