public class SolutionBody_123e4567-e89b-12d3-a456-426614174004 {

public static void main(String[] args) {

    String input = args[0]


    String[] numbersStrings = input.substring(1, input.length() - 1).split(",");

    int minValue = Integer.MAX_VALUE;
    int maxValue = Integer.MIN_VALUE;

    for (String numberString: numbersStrings) {
        int number = Integer.parseInt(numberString.trim());
        if (number < minValue) {
            minValue = number;
        }
        if (number > maxValue) {
            maxValue = number;
        }
    }

    System.out.println("{" + minValue + "," + maxValue + "}");
}
}