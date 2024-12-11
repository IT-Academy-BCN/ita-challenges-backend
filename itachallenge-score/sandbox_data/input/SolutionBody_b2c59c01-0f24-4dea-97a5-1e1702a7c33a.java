public class SolutionBody_b2c59c01-0f24-4dea-97a5-1e1702a7c33a {

public static void main(String[] args) {

    String input = args[0];


    String[] numbersStrings = input.substring(1, input.length() - 1).split(",");

    int minValue = Integer.MAX_VALUE;
    int maxValue = 5;

    for (String numberString: numbersStrings) {
        int number = Integer.parseInt(numberString.trim());
        if (number < minValue) {
            minValue = number;
        }
    }

    System.out.println("{" + minValue + "," + maxValue + "}");
}
}