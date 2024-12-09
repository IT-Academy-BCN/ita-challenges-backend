package com.itachallenge.score.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObfuscationDetector {

    private ObfuscationDetector() {
        throw new IllegalStateException("Utility class");
    }

    public static ExecutionResult detectAndModify(String sourceCode) {
        String transformedCode = applyConcatenation(sourceCode);
        transformedCode = applyToUpperCase(transformedCode);
        transformedCode = applyToLowerCase(transformedCode);
        transformedCode = applySubstring(transformedCode);
        transformedCode = applyCapitalize(transformedCode);
        transformedCode = applyReplace(transformedCode);
        transformedCode = applyReplaceAll(transformedCode);
        transformedCode = applyFormat(transformedCode);
        transformedCode = applyStringBuilderAppend(transformedCode);
        transformedCode = applyStringBuilderReverse(transformedCode);
        transformedCode = applyStringBufferAppend(transformedCode);
        transformedCode = applyStringFormat(transformedCode);
        transformedCode = applyStringJoin(transformedCode);
        transformedCode = applyStringValueOf(transformedCode);
        transformedCode = applySplit(transformedCode);
        transformedCode = applyToCharArray(transformedCode);
        transformedCode = applyCharacterToUpperCase(transformedCode);
        transformedCode = applyCharacterToLowerCase(transformedCode);
        transformedCode = applyEndsWith(transformedCode);
        transformedCode = applyStartsWith(transformedCode);
        transformedCode = applyIndexOf(transformedCode);
        transformedCode = applyMatches(transformedCode);
        transformedCode = applyIntern(transformedCode);
        transformedCode = applyGetBytes(transformedCode);
        transformedCode = applyStringBuilderDelete(transformedCode);
        transformedCode = applyStringBufferDelete(transformedCode);
        transformedCode = applyStringTokenizer(transformedCode);

        ExecutionResult result = new ExecutionResult();
        result.setMessage(transformedCode);
        result.setSuccess(true);
        result.setCompiled(true);
        result.setExecution(true);

        return result;
    }

    protected static String applyConcatenation(String code) {
        String transformedCode = code;
        boolean changed;
        do {
            String oldCode = transformedCode;
            transformedCode = transformedCode.replaceAll("\"([^\"]+)\"\\s*\\+\\s*(\\w+)", "$1 + $2");
            changed = !transformedCode.equals(oldCode);
        } while (changed);

        return transformedCode;
    }


}

