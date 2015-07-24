package com.app;

/**
 * Created by yuzhitao on 7/13/2015.
 */
public class Test {
    public static void main(String[] args) {
        final StringBuilder buffer = new StringBuilder();
        String str = "abc\ndef\nmsn\nxyz";

        int i = 0;
        String header = "HHHH";
        String id = "IDID";
        while (i++ < str.length()) {
            int ch = str.charAt(i-1);
            if (ch == 13) {
                buffer.append("[\\r]");
            } else if (ch == 10) {
                buffer.append("[\\n]\"");
                buffer.insert(0, "\"");
                buffer.insert(0, header);
                System.out.println(id + " " + buffer.toString());
                buffer.setLength(0);
            }
        }

        String str1 = "abc%sdef";

        System.out.println(String.format(str1, "MMM"));
    }
}
