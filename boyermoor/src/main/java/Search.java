import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Search {

    public static List<Integer> boyerMoor(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        HashMap<Character, Integer> processed = new HashMap<>();
        for (int i = 0; i < pattern.length() - 1; i++) {
            processed.put(pattern.charAt(i), pattern.length() - i - 1);
        }

        int shift = 0;
        while (shift <= text.length() - pattern.length()) {
            int j = pattern.length() - 1;
            while (j >= 0 && text.charAt(shift + j) == pattern.charAt(j)) {
                j--;
            }

            if (j < 0) {
                matches.add(shift);
                shift++;
            } else {
                    Integer index = processed.get(text.charAt(shift + j));
                    int addition = (pattern.length() - j - 1);
                    if (index == null || index < addition) {
                        shift += pattern.length() - (pattern.length() - j) + 1;
                    } else {
                        shift += index - addition;
                    }
                }
            }

        return matches;
    }

    public static int[] findPrefixes(String s) {
        int[] prefixes = new int[s.length()];

        for (int i = 0; i < s.length(); i++) {
            for (int j = i; j < s.length(); j++) {
                if (s.charAt(j - i) == s.charAt(j)) {
                    prefixes[i]++;
                } else {
                    break;
                }
            }
        }

        return prefixes;
    }
}
