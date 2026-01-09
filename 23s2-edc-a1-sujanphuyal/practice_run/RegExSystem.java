/* This program cannot handle the grouping in the regular expression. It is capable of operating on regular expressions without any parentheses character class*/

/*

import java.util.Scanner;

public class RegExSystem {
    private static boolean shouldRun = true;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter a regular expression:");
        String regexInput = scanner.nextLine();
        System.out.println("Ready");

        // Register a shutdown hook to handle Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gracefully...");
            shouldRun = false;  // Signal the loop to stop
        }));

        while (shouldRun) {
            String input = scanner.nextLine();
            if (input.isEmpty()) {
                break;
            }

            boolean matches = false;

            if (regexInput.contains("|")) {
                String[] regexParts = regexInput.split("\\|");
                for (String regexPart : regexParts) {
                    if (customPatternMatching(input, regexPart)) {
                        matches = true;
                        break;
                    }
                }
            } else {
                matches = customPatternMatching(input, regexInput);
            }

            System.out.println(matches);
        }

        scanner.close();
    }

    private static boolean customPatternMatching(String input, String regex) {
        return customMatches(input, regex, 0, 0);
    }

    private static boolean customMatches(String input, String regex, int i, int j) {
        if (j == regex.length()) {
            return i == input.length();
        }

        boolean firstMatch = i < input.length() && (input.charAt(i) == regex.charAt(j) || regex.charAt(j) == '.');

        if (j + 1 < regex.length() && regex.charAt(j + 1) == '*') {
            return (customMatches(input, regex, i, j + 2) ||
                    (firstMatch && customMatches(input, regex, i + 1, j)));
        }

        if (firstMatch) {
            if (j + 1 < regex.length() && regex.charAt(j + 1) == '+') {
                return customMatches(input, regex, i + 1, j) || customMatches(input, regex, i + 1, j + 2);
            }
            return customMatches(input, regex, i + 1, j + 1);
        }

        return false;
    }
}



 */