import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatExample {
    public static void main(String[] args) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            System.out.println("Formatted date: " + sdf.format(date));
        } catch (Exception e) {
            System.out.println("Expected failure: " + e.getMessage());
        }
    }
}