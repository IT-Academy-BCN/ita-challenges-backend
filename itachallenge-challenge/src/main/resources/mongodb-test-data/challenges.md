## EASY
1. Descending Order
   - Your task is to make a function that can take any non-negative integer as an argument and return it with its digits in descending order. Essentially, rearrange the digits to create the highest possible number.
   - Example:
     - Input: 42145 Output: 54421
       Input: 145263 Output: 654321
       Input: 123456789 Output: 987654321
   - [Link](https://www.codewars.com/kata/5467e4d82edf8bbf40000155)


2. Highest and Lowest
   - In this little assignment you are given a string of space separated numbers, and have to return the highest and lowest number.
   - Example: 
     - highAndLow("1 2 3 4 5")  // return "5 1"
       highAndLow("1 2 -3 4 5") // return "5 -3"
       highAndLow("1 9 3 4 -5") // return "9 -5"
  
    - [link](https://www.codewars.com/kata/554b4ac871d6813a03000035)


3. You're a square!
   - A square of squares
     You like building blocks. You especially like building blocks that are squares. And what you even like more, is to arrange them into a square of square building blocks!

       However, sometimes, you can't arrange them into a square. Instead, you end up with an ordinary rectangle! Those blasted things! If you just had a way to know, whether you're currently working in vain… Wait! That's it! You just have to check if your number of building blocks is a perfect square.
    
       Task
       Given an integral number, determine if it's a square number:
    
       In mathematics, a square number or perfect square is an integer that is the square of an integer; in other words, it is the product of some integer with itself.
    
       The tests will always use some integral number, so don't worry about that in dynamic typed languages.

   - Example:
     - -1  =>  false
       0  =>  true
       3  =>  false
       4  =>  true
       25  =>  true
       26  =>  false
    - [Link](https://www.codewars.com/kata/54c27a33fb7da0db0100040e)


4. List Filtering
   - In this kata you will create a function that takes a list of non-negative integers and strings and returns a new list with the strings filtered out.
   - Example:
     - Kata.filterList(List.of(1, 2, "a", "b")) => List.of(1,2)
       Kata.filterList(List.of(1, 2, "a", "b", 0, 15)) => List.of(1,2,0,15)
       Kata.filterList(List.of(1, 2, "a", "b", "aasf", "1", "123", 231)) => List.of(1, 2, 231)
   - [Link](https://www.codewars.com/kata/53dbd5315a3c69eed20002dd)


5. Isograms
   - An isogram is a word that has no repeating letters, consecutive or non-consecutive. Implement a function that determines whether a string that contains only letters is an isogram. Assume the empty string is an isogram. Ignore letter case.
       Example: (Input --> Output)
       "Dermatoglyphics" --> true "aba" --> false "moOse" --> false (ignore letter case)
   - Example:
     - isIsogram "Dermatoglyphics" = true
       isIsogram "moose" = false
       isIsogram "aba" = false
   - [Link](https://www.codewars.com/kata/54ba84be607a92aa900000f1)


6. Exes and Ohs
   - Check to see if a string has the same amount of 'x's and 'o's. The method must return a boolean and be case insensitive. The string can contain any char.
   - Example:
     - XO("ooxx") => true
       XO("xooxx") => false
       XO("ooxXm") => true
       XO("zpzpzpp") => true // when no 'x' and 'o' is present should return true
       XO("zzoo") => false
   - [Link](https://www.codewars.com/kata/55908aad6620c066bc00002a)


7. Get the Middle Character
   - You are going to be given a word. Your job is to return the middle character of the word. If the word's length is odd, return the middle character. If the word's length is even, return the middle 2 characters.
   - Example:
     - Kata.getMiddle("test") should return "es"
       Kata.getMiddle("testing") should return "t"
       Kata.getMiddle("middle") should return "dd" 
       Kata.getMiddle("A") should return "A"
   - [Link](https://www.codewars.com/kata/56747fd5cb988479af000028)  



## MEDIUM

1. Simple Pig Latin
   - Move the first letter of each word to the end of it, then add "ay" to the end of the word. Leave punctuation marks untouched.
   - Example
     - pigIt('Pig latin is cool'); // igPay atinlay siay oolcay
       pigIt('Hello world !');     // elloHay orldway !
   - [Link](https://www.codewars.com/kata/520b9d2ad5c005041100000f) 

2. RGB To Hex Conversion
   - The rgb function is incomplete. Complete it so that passing in RGB decimal values will result in a hexadecimal representation being returned. Valid decimal values for RGB are 0 - 255. Any values that fall out of that range must be rounded to the closest valid value.
    Note: Your answer should always be 6 characters long, the shorthand with 3 will not work here.
   - Example:
     - 255, 255, 255 --> "FFFFFF"
       255, 255, 300 --> "FFFFFF"
       0, 0, 0       --> "000000"
       148, 0, 211   --> "9400D3"
   - [Link](https://www.codewars.com/kata/513e08acc600c94f01000001)


3. Scramblies
   - Complete the function scramble(str1, str2) that returns true if a portion of str1 characters can be rearranged to match str2, otherwise returns false.
       Notes:
           Only lower case letters will be used (a-z). No punctuation or digits will be included.
           Performance needs to be considered.
   - Example: 
     - scramble('rkqodlw', 'world') ==> True
       scramble('cedewaraaossoqqyt', 'codewars') ==> True
       scramble('katas', 'steak') ==> False
   - [Link](https://www.codewars.com/kata/55c04b4cc56a697bb0000048)


4. Number of trailing zeros of N!
   - Write a program that will calculate the number of trailing zeros in a factorial of a given number.
    N! = 1 * 2 * 3 *  ... * N
    Be careful 1000! has 2568 digits...
   - Example:
     - zeros(6) = 1
        6! = 1 * 2 * 3 * 4 * 5 * 6 = 720 --> 1 trailing zero
     - zeros(12) = 2
        12! = 479001600 --> 2 trailing zeros
   - [Link](https://www.codewars.com/kata/52f787eb172a8b4ae1000a34)

## Error    

5. Isograms
    - An isogram is a word that has no repeating letters, consecutive or non-consecutive. Implement a function that determines whether a string that contains only letters is an isogram. Assume the empty string is an isogram. Ignore letter case.
      Example: (Input --> Output)
      "Dermatoglyphics" --> true "aba" --> false "moOse" --> false (ignore letter case)
    - Example:
        - isIsogram "Dermatoglyphics" = true
          isIsogram "moose" = false
          isIsogram "aba" = false
    - [Link](https://www.codewars.com/kata/54ba84be607a92aa900000f1)
   

5. Going to zero or to infinity?
    - Calculate (1 / n!) * (1! + 2! + 3! + ... + n!) for a given n, where n is an integer greater or equal to 1.
   
    - Example:
         - 1.0000989217538616 will be truncated to 1.000098
           1.2125000000000001 will be truncated to 1.2125
    - [Link](https://www.codewars.com/kata/55a29405bc7d2efaff00007c)


5. Find the smallest
    - You have a positive number n consisting of digits. You can do at most one operation: Choosing the index of a digit in the number, remove this digit at that index and insert it back to another or at the same place in the number in order to find the smallest number you can get.
    Task:
    Return an array or a tuple or a string depending on the language (see "Sample Tests") with
    
    the smallest number you got
    the index i of the digit d you took, i as small as possible
    the index j (as small as possible) where you insert this digit d to have the smallest number.
       
    - Example:
         - smallest(261235) --> [126235, 2, 0] or (126235, 2, 0) or "126235, 2, 0"
        smallest(209917) --> [29917, 0, 1]
        smallest(1000000) --> [1, 0, 6]
    - [Link](https://www.codewars.com/kata/573992c724fc289553000e95)
   

7. Land perimeter
    - Given an array arr of strings, complete the function by calculating the total perimeter of all the islands. Each piece of land will be marked with 'X' while the water fields are represented as 'O'. Consider each tile being a perfect 1 x 1 piece of land. Some examples for better visualization:
   
    - Example:
      ['XOOXO',
      'XOOXO',
      'OOOXO',
      'XXOXO',
      'OXOOO']  -> "Total land perimeter: 24"
- ["XOOO",
  "XOXO",
  "XOXO",
  "OOXX",
  "OOOO"]  -> "Total land perimeter: 18"
   
  - [Link](https://www.codewars.com/kata/573992c724fc289553000e95)








    
## HARD

1. Evaluate mathematical expression.
    - Given a mathematical expression as a string you must return the result as a number.
    - Example:
        - 1 -1   // 0
        - 1 - -1 // 2
    - [Link](https://www.codewars.com/kata/52a78825cdfc2cfc87000005)


2. The Millionth Fibonacci Kata.
    - In this kata you will have to calculate fib(n) where:
      fib(0) := 0
      fib(1) := 1
      fib(n + 2) := fib(n + 1) + fib(n)
   

3. Title: Compress String

    Statement: Write a program that takes a string of characters and compresses it by removing consecutive character repetitions. For example, "aaabbbccc" would be compressed as "a3b3c3".
    
    Example:
    
    Input: "aaabbbccc"
    Expected Output: "a3b3c3"


4. Title: Longest Common Subsequence

    Statement: Implement a program that finds the longest common subsequence between two given strings. A subsequence is a sequence of characters that appears in the same relative order in both strings.
    
    Example:
    
    Input: "ABCBDAB", "BDCAB"
    Expected Output: "BCAB"
    
    https://www.codewars.com/kata/52756e5ad454534f220001ef


5. Title: Find the Equilibrium Point in an Array

    Statement: Write a program that finds the equilibrium point in an array, which is an index where the sum of elements to the left is equal to the sum of elements to the right.
    
    Example:
    
    Input: [1, 2, 3, 4, 6]
    Expected Output: 3


6. Validate Mathematical Expressions

    Statement: Create a program that validates whether a given mathematical expression is valid, considering balanced parentheses and valid mathematical operators.
    
    Example:
    
    Input: "((3 + 4) * 2) / (5 - 2)"
    Expected Output: Valid


7. Find the Largest Contiguous Subarray

    Statement: Implement an algorithm to find the largest contiguous subarray in an array of integers.
    
    Example:
    
    Input: [-2, 1, -3, 4, -1, 2, 1, -5, 4]
    Expected Output: [4, -1, 2, 1]


8. Count Inversions in an Array

    Statement: Write a program that counts the number of inversions in an array. An inversion occurs when a pair of elements in the array is in the wrong order.
    
    Example:
    
    Input: [2, 4, 1, 3, 5]
    Expected Output: 3

9. Find the Shortest Path in a Weighted Graph

    Statement: Implement an algorithm to find the shortest path between two nodes in a weighted graph using Dijkstra's algorithm.
    
    Example:
    
    Input: Graph with weights
    Expected Output: Shortest path between two nodes
