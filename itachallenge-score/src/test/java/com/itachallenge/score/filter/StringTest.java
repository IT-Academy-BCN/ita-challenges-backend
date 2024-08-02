package com.itachallenge.score.filter;

public class StringTest {


    public static final String ASCIIvalid = """
                  import java.util.Arrays;
                    
            public class Main {
                public static void main(String[] args) {
                    String numbers = "3,1,4,1,5,9";
                    int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
                    Arrays.sort(numArray);
                    System.out.println(Arrays.toString(numArray));
                }
            }
                """;

    public static final String ASCIIinvalid = """
                              import java.util.Arrays;
                    
            public class βain {
                public static void main(String[] args) {
                    String numbers = "©, 3,1,4,1,5,9";
                    int[] numArray = Arrays.stream(numbers.split(",")).mapToInt(Integer::parseInt).toArray();
                    Arrays.sort(numArray);
                    System.out.println(Arrays.toString(numArray)); """;
}
