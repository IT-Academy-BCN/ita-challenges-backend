
import
public class SolutionBody_123e4567-e89b-12d3-a456-426614174000 {

    public static void main(String[] args) {
        String input = args[0];

        int[] numbers = Arrays.(input.split(",")).mapToInt(Integer::parseInt).toArray();

        int minValue = Arrays.stream(numbers).min().orElse(Integer.MIN_VALUE);
        int maxValue = Arrays.stream(numbers).max().orElse(Integer.MAX_VALUE);

        System.out.println(minValue + "," + maxValue);
    }
}