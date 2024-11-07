import jdk.management.cmm.SystemResourcePressureMXBean;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SystemResourcePressureExample {
    public static void main(String[] args) {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            ObjectName objectName = new ObjectName("jdk.management.cmm:type=SystemResourcePressure");

            SystemResourcePressureMXBean resourcePressureMXBean =
                    ManagementFactory.newPlatformMXBeanProxy(
                            mBeanServer,
                            objectName.toString(),
                            SystemResourcePressureMXBean.class
                    );

            System.out.println("Heap Memory Pressure: " + resourcePressureMXBean.getHeapMemoryPressure());
            System.out.println("Non-Heap Memory Pressure: " + resourcePressureMXBean.getNonHeapMemoryPressure());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}