package com.itachallenge.score.util;

import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObfuscationDetector {

    private ObfuscationDetector() {
        throw new IllegalStateException("Utility class");
    }

    public static ExecutionResult detectAndModify(String sourceCode) {
        String transformedCode = applyConcatenation(sourceCode);
        transformedCode = applySubstring(transformedCode);
        transformedCode = applyToUpperCase(transformedCode);
        transformedCode = applyToLowerCase(transformedCode);
        transformedCode = applyCharacterToUpperOrLowerCase(transformedCode);
//        transformedCode = applyReplaceStringLiterals(transformedCode);
//        transformedCode = applyReplaceAll(transformedCode);
//        transformedCode = applyFormat(transformedCode);
//        transformedCode = applyStringBuilderAppend(transformedCode);
//        transformedCode = applyStringBuilderReverse(transformedCode);
//        transformedCode = applyStringBufferAppend(transformedCode);
//        transformedCode = applyStringBuilderDelete(transformedCode);
//        transformedCode = applyStringBufferDelete(transformedCode);
//        transformedCode = applyStringFormat(transformedCode);
//        transformedCode = applyStringJoin(transformedCode);
//        transformedCode = applyStringValueOf(transformedCode);
//        transformedCode = applySplit(transformedCode);
//        transformedCode = applyToCharArray(transformedCode);
//        transformedCode = applyEndsWith(transformedCode);
//        transformedCode = applyStartsWith(transformedCode);
//        transformedCode = applyIndexOf(transformedCode);
//        transformedCode = applyMatches(transformedCode);
//        transformedCode = applyIntern(transformedCode);
//        transformedCode = applyGetBytes(transformedCode);
//        transformedCode = applyStringTokenizer(transformedCode);

        ExecutionResult result = new ExecutionResult();
        result.setMessage(transformedCode);

        return result;
    }

    protected static String applyStringMethodTransformation(
            String code, String methodName, UnaryOperator<String> transformation
    ) {
        String transformedCode = code;
        String regex = "\"([^\"]*)\"." + methodName + "\\(\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(transformedCode);

        while (matcher.find()) {
            String matchedString = matcher.group(1);
            String transformedString = transformation.apply(matchedString);
            transformedCode = transformedCode.replace(matcher.group(0), "\"" + transformedString + "\"");
        }

        return transformedCode;
    }

    protected static String applyConcatenation(String code) {
        String transformedCode = code;
        boolean changed;
        do {
            String oldCode = transformedCode;
            transformedCode = transformedCode.replaceAll(
                    "\"([^\"]+)\"\\s*\\+\\s*\"([^\"]+)\"", "\"$1$2\""
            );
            transformedCode = transformedCode.replaceAll(
                    "\"([^\"]+)\"\\.concat\\(\"([^\"]+)\"\\)", "\"$1$2\""
            );
            changed = !transformedCode.equals(oldCode);
        } while (changed);

        return transformedCode;
    }

    protected static String applySubstring(String code) {
        String transformedCode = code;

        while (transformedCode.contains(".substring(")) {
            String regex = "(\\w+)\\.substring\\((\\d+),\\s*(\\d+)\\)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(transformedCode);

            while (matcher.find()) {
                String strBeforeSubstring = matcher.group(1);
                int startIdx = Integer.parseInt(matcher.group(2));
                int endIdx = Integer.parseInt(matcher.group(3));
                String substring = strBeforeSubstring.substring(startIdx, endIdx);
                transformedCode = transformedCode.replace(matcher.group(0), substring);
            }
        }

        return transformedCode;
    }

    protected static String applyToUpperCase(String code) {
        return applyStringMethodTransformation(code, "toUpperCase", String::toUpperCase);
    }

    protected static String applyToLowerCase(String code) {
        return applyStringMethodTransformation(code, "toLowerCase", String::toLowerCase);
    }

    protected static String applyCharacterToUpperOrLowerCase(String code) {
        String transformedCode = code;
        // Updated regex to capture the full method name ("toUpperCase" or "toLowerCase")
        String regex = "Character\\.to(Upper|Lower)Case\\('([^']+)'\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(transformedCode);

        while (matcher.find()) {
            String character = matcher.group(2);
            String transformedCharacter;
            if (matcher.group(1).equals("Upper")) {
                transformedCharacter = character.toUpperCase();
            } else {
                transformedCharacter = character.toLowerCase();
            }
            transformedCode = transformedCode.replace(matcher.group(0), transformedCharacter);
        }

        return transformedCode;
    }

//    protected static String applyCharacterToUpperOrLowerCase(String code) {
//        String transformedCode = code;
//        String regex = "Character\\.to((UpperCase)|(LowerCase))\\('([^'])'\\)";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(transformedCode);
//
//        while (matcher.find()) {
//            String character = matcher.group(2);
//            System.out.println("Group 2: " + character);
//            String transformedCharacter = character.toUpperCase();
//            System.out.println("transformedCharacter: " + transformedCharacter);
//            transformedCode = transformedCode.replace(
//                    matcher.group(0),
//                    transformedCode.replace(matcher.group(0),
//                            "Character.to" + matcher.group(1) + "('" + transformedCharacter + "')"));
//        }
//        return transformedCode;
//    }

//    public static String applyReplaceStringLiterals(String code) {
//        return applyStringMethodTransformation(code, "replace", (matchedString, args) -> {
//            if (matchedString.matches("'\\w', '\\w'")) {
//                char oldChar = matchedString.charAt(1);
//                char newChar = matchedString.charAt(4);
//                return matchedString.replace(oldChar, newChar);
//            } else {
//                String[] splitArgs = args.split(",");
//                String oldValue = splitArgs[0].trim().replace("\"", "");  // Handle removing quotes for substrings
//                String newValue = splitArgs[1].trim().replace("\"", "");  // Handle removing quotes for substrings
//
//                // Perform the actual substring replacement
//                return "\"" + matchedString.replace(oldValue, newValue) + "\"";  // Return the replaced string
//            }
//        });
//    }


}

