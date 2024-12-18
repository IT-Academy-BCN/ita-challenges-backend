import javax.script.ScriptEngineManager;

public class ScriptingExample {
    public static void main(String[] args) {
        ScriptEngineManager manager = new ScriptEngineManager();
        System.out.println("Scripting module test");
    }
}

/* Expected output:
*
** Error: Could not find or load main class ScriptingExample
** Caused by: java.lang.NoClassDefFoundError: javax/script/ScriptEngineManager */