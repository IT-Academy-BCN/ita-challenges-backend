
import java.util.Arrays;
public class SolutionBody {

    public static void main(String[] args) {
        String input = args[0];

        int[] numbers = Arrays.stream(input.split(",")).mapToInt(Integer::parseInt).toArray();

        int minValue = Arrays.stream(numbers).min().orElse(Integer.MIN_VALUE);
        int maxValue = Arrays.stream(numbers).max().orElse(Integer.MAX_VALUE);

        System.out.println(minValue + "," + maxValue);
    }
}