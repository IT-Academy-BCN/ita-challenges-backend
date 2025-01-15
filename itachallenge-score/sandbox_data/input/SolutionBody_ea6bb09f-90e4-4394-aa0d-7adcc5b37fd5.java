public class SolutionBody_ea6bb09f-90e4-4394-aa0d-7adcc5b37fd5 {

    public static void main(String[] args) {

        String input = args[0];


        String[] numbersStrings = input.substring(1, input.length() - 1).split(",");

        int minValue = Integer.MAX_VALUE;
        int maxValue = Integer.MIN_VALUE;

        for (String numberString: numbersStrings) {
            int number = Integer.parseInt(numberString.trim());
            if (number < minValue) {
                minValue = number;
            }
            if (number > minValue) {
                minValue = number;
            }
        }

        System.out.println("{" + minValue + "," + maxValue + "}");
    }
}