package com.generationjava.util;

import java.util.ArrayList;

public class WildcardUtils {

    public static boolean match(String text, String wildcard) {
        // split wildcard on ? and *
        // for each element of the array, find a matching block in text
        // earliest matching block counts
        String[] wcs = splitOnTokens(wildcard);
        int textIdx = 0;
        for(int i=0; i<wcs.length; i++) {
            if(textIdx == text.length()) {
                if("*".equals(wcs[i])) {
                    return true;
                }
                if("?".equals(wcs[i])) {
                    return true;
                }
                return wcs[i].length() == 0;
            }

            if("?".equals(wcs[i])) {
                textIdx++;
            } else
            if("*".equals(wcs[i])) {
                int nextIdx = i+1;
                if(nextIdx == wcs.length) {
                    return true;
                }
                int restartIdx = text.indexOf(wcs[nextIdx], textIdx);
                if(restartIdx == -1) {
                    return false;
                } else {
                    textIdx = restartIdx;
                }
            } else {
                if(!text.startsWith(wcs[i], textIdx)) {
                    return false;
                } else {
                    textIdx += wcs[i].length();
                }
            }
        }

        return true;
    }

    static String[] splitOnTokens(String text) {
        char[] array = text.toCharArray();
        if(text.indexOf("?") == -1 && text.indexOf("*") == -1) {
            return new String[] { text };
        }

        ArrayList list = new ArrayList();
        StringBuffer buffer = new StringBuffer();
        for(int i=0; i<array.length; i++) {
            if(array[i] == '?' || array[i] == '*') {
                if(buffer.length() != 0) {
                   list.add(buffer.toString());
                   buffer.setLength(0);
                }
                list.add(new String( new char[] { array[i] } ));
            } else {
                buffer.append(array[i]);
            }
        }
        if(buffer.length() != 0) {
            list.add(buffer.toString());
        }

        return (String[]) list.toArray(new String[0]);
    }
        

}
