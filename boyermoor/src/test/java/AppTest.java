import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static junit.framework.TestCase.assertEquals;

public class AppTest {

    @Test
    public void boyerMoorHorspulTest() {
        InputStream testCases = this.getClass()
                .getClassLoader()
                .getResourceAsStream("string_matching_test_cases_31272_751472-57251-751472.tsv");
        Scanner scanner = new Scanner(testCases);
        int expectedCount = Integer.parseInt(scanner.nextLine());

        int count = 0;
        while (scanner.hasNext()) {
            String[] testString = scanner.nextLine().split("\t");
            String string = testString[0];
            String pattern = testString[1];
            List<Integer> occurrences = new ArrayList<>();
            if (testString.length == 3) {
                for (String number : testString[2].split(" ")) {
                    occurrences.add(Integer.parseInt(number));
                }
            }

            List<Integer> list = Search.boyerMoor(string, pattern);
            assertEquals(occurrences, list);
            count++;
        }

        assertEquals(expectedCount, count);
    }

    @Test
    public void findPrefixesTest() {
        InputStream testCases = this.getClass()
                .getClassLoader()
                .getResourceAsStream("prefixes_test.tsv");
        Scanner scanner = new Scanner(testCases);
        int expectedCount = Integer.parseInt(scanner.nextLine());

        int count = 0;
        while (scanner.hasNext()) {
            count++;
            String[] testString = scanner.nextLine().split("\t");
            String string = testString[0];
            int[] expected = new int[string.length()];
            String[] nums = testString[1].split(" ");
            for (int i = 0; i < nums.length; i++) {
                expected[i] = Integer.parseInt(nums[i]);
            }

            int[] found = Search.findPrefixes(string);

            assertEquals(expected.length, found.length);

            for (int i = 0; i < found.length; i++) {
                assertEquals(expected[i], found[i]);
            }
        }

        assertEquals(expectedCount, count);
    }
}