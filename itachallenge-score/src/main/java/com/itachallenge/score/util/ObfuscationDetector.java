package com.itachallenge.score.util;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObfuscationDetector {

    private ObfuscationDetector() {
        throw new IllegalStateException("Utility class");
    }

    public static ExecutionResult detectAndModify(String sourceCode) {
        String transformedCode = applyConcatenation(sourceCode);
        transformedCode = applySubstring(transformedCode);
        transformedCode = applyCharacterToUpperOrLowerCase(transformedCode);
        transformedCode = applyToUpperOrLowerCase(transformedCode);
        transformedCode = applyReplaceStringLiterals(transformedCode);
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

    // Helper method used by the methods that deobfuscate a String method
    protected static String applyStringMethodTransformation(
            String code, String regex, Function<MethodDetails, String> transformation
    ) {
        String transformedCode = code;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(transformedCode);

        String previousCode;
        do {
            previousCode = transformedCode;
            matcher.reset();

            while (matcher.find()) {
                String beforeMethod = matcher.group(1);
                String methodName = matcher.group(2);
                String parameters = matcher.group(3);

                MethodDetails methodDetails = new MethodDetails(beforeMethod, methodName, parameters);
                String transformedMethodCall = transformation.apply(methodDetails);
                transformedCode = transformedCode.replace(matcher.group(0), transformedMethodCall);
            }

        } while (!transformedCode.equals(previousCode));

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

    protected static String applyCharacterToUpperOrLowerCase(String code) {
        String transformedCode = code;
        String regex = "Character\\.to(Upper|Lower)Case\\('([^']+)'\\)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(transformedCode);

        while (matcher.find()) {
            String character = matcher.group(2);
            String transformedCharacter;
            if (matcher.group(1).equals("Upper")) {
                transformedCharacter = "'" + character.toUpperCase() + "'";
            } else {
                transformedCharacter = "'" + character.toLowerCase()+ "'";
            }
            transformedCode = transformedCode.replace(matcher.group(0), transformedCharacter);
        }

        return transformedCode;
    }

    public static String applyToUpperOrLowerCase(String code) {
        String regex = "\"([^\"]+)\"\\.(toUpperCase|toLowerCase)\\(([^)]*)\\)";
        String transformedCode = code;

        while (transformedCode.contains(".toUpperCase(") || transformedCode.contains(".toLowerCase(")) {
            transformedCode = applyStringMethodTransformation(transformedCode, regex, methodDetails -> {
                String beforeMethod = methodDetails.getBeforeMethod();
                String methodName = methodDetails.getMethodName();

                if (methodName.equals("toUpperCase")) {
                    return "\"" + beforeMethod.toUpperCase() + "\"";
                } else {
                    return "\"" + beforeMethod.toLowerCase() + "\"";
                }
            });
        }

        return transformedCode;
    }

    public static String applyReplaceStringLiterals(String code) {
        String regex = "\"([^\"]+)\"\\.(replace|replaceAll)\\(([^)]*)\\)";
        String transformedCode = code;

        while (transformedCode.contains(".replace(")  || transformedCode.contains(".replaceAll(")) {
            transformedCode = applyStringMethodTransformation(transformedCode, regex, methodDetails -> {
                String beforeMethod = methodDetails.getBeforeMethod();
                String[] params = methodDetails.getParameters().split(",");
                String target = params[0].trim().replace("\"", "").replace("'", "");
                String replacement = params[1].trim().replace("\"", "").replace("'", "");
                String methodName = methodDetails.getMethodName();
                if (methodName.equals("replace")) {
                    return "\"" + beforeMethod.replace(target, replacement) + "\"";
                } else {
                    return "\"" + beforeMethod.replaceAll(target, replacement) + "\"";
                }
            });
        }
        return transformedCode;
    }

//    public static String applyReplaceAllStringLiterals(String code) {
//        // Regex to capture the replaceAll() method call
//        String regex = "\"([^\"]+)\"\\.replaceAll\\(([^,]+),\\s*([^\")]+)\\)";
//        String transformedCode = code;
//
//        // Process all replaceAll() calls recursively until no more exist
//        while (transformedCode.contains(".replaceAll(")) {
//            transformedCode = applyStringMethodTransformation(transformedCode, regex, methodDetails -> {
//                String beforeMethod = methodDetails.getBeforeMethod();
//                String[] params = methodDetails.getParameters().split(",");
//                String regexPattern = params[0].trim().replace("\"", "").replace("'", "");
//                String replacement = params[1].trim().replace("\"", "").replace("'", "");
//
//                // Apply the regex-based replacement
//                return "\"" + beforeMethod.replaceAll(regexPattern, replacement) + "\"";
//            });
//        }
//
//        return transformedCode;
//    }


}

