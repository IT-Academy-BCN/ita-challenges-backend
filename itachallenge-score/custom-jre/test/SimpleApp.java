import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SimpleApp {

    public static void main(String[] args) {

        List<String> items = new ArrayList<>();
        items.add("Item 1");
        items.add("Item 2");
        items.add("Item 3");

        System.out.println("Lista de elementos:");
        for (String item : items) {
            System.out.println(item);
        }

        LocalDate today = LocalDate.now();
        LocalDate oneWeekLater = today.plusDays(7);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        System.out.println("\nFecha actual: " + today.format(formatter));
        System.out.println("Fecha en una semana: " + oneWeekLater.format(formatter));

        String message = "Hello, Custom Compiler!";
        System.out.println("\nMensaje en mayúsculas: " + message.toUpperCase());
    }
}