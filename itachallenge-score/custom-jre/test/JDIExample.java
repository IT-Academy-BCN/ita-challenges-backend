import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.Bootstrap;

public class JDIExample {
    public static void main(String[] args) {
        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
        System.out.println("Virtual Machine Manager: " + vmm);
    }
}

/* Expected output:
**
** Exception in thread "main" java.lang.NoClassDefFoundError: com/sun/jdi/Bootstrap
**        at JDIExample.main(JDIExample.java:6)
** Caused by: java.lang.ClassNotFoundException: com.sun.jdi.Bootstrap
**         at java.base/jdk.internal.loader.BuiltinClassLoader.loadClass(Unknown Source)
**         at java.base/jdk.internal.loader.ClassLoaders$AppClassLoader.loadClass(Unknown Source)
**         at java.base/java.lang.ClassLoader.loadClass(Unknown Source)
**         ... 1 more */