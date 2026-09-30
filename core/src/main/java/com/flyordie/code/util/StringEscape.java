package com.flyordie.code.util;

public class StringEscape {

    private StringEscape() {
    }

    public static String escapeJSString(String source) {
        return escapeJSString(source, false);
    }

    public static String escapeJSONString(String source) {
        return escapeJSString(source, true);
    }

    private static String escapeJSString(String source, boolean json) {
        int size = source.length();
        StringBuilder buffer = new StringBuilder(size * 2);
        int i = 0;

        while (i < size) {
            char ch = source.charAt(i++);

            if (ch == '"')
                buffer.append("\\\"");
            else if (ch == '\'' && !json)
                buffer.append("\\'");
            else if (ch == '\\')
                buffer.append("\\\\");
            else if (ch == '\b')
                buffer.append("\\b");
            else if (ch == '\t')
                buffer.append("\\t");
            else if (ch == '\n')
                buffer.append("\\n");
            else if (ch == '\u000B') // vertical tab
                buffer.append("\\v");
            else if (ch == '\f')
                buffer.append("\\f");
            else if (ch == '\r')
                buffer.append("\\r");
            else if (ch == '\u2028') // Line separator
                buffer.append("\\u2028");
            else if (ch == '\u2029') // Paragraph separator
                buffer.append("\\u2029");
            else
                buffer.append(ch);
        }

        return buffer.toString();
    }

}
