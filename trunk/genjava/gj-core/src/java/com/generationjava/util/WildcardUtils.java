package com.generationjava.util;

public class WildcardUtils {

    public static boolean match(String text, String wildcard) {
        int wildcardIdx = 0;
        boolean globbing = false;
        for( int textIdx=0; textIdx < text.length(); textIdx++) {
            if(wildcardIdx == wildcard.length()) {
                return globbing;
            }
            char ch = wildcard.charAt(wildcardIdx);
            char th = text.charAt(textIdx);

            if(globbing) {
                if(ch == th) {
                    wildcardIdx++;
                    globbing = false;
                }
            } else
            if(ch == '?') {
                wildcardIdx++;
            } else
            if(ch == '*') {
                globbing = true;
                wildcardIdx++;
            } else 
            if(ch != th) {
                return false;
            } else {
                wildcardIdx++;
            }
        }
        return true;
    }

}
