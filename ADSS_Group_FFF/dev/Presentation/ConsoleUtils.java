// src/Presentation/ConsoleUtils.java
package Presentation;

public class ConsoleUtils {
    /**
     * Prints text one character at a time, pausing `delayMs` milliseconds between each.
     */
    public static void typewriterPrint(String text, int delayMs) {
        for (char c : text.toCharArray()) {
            System.out.print(c);
            System.out.flush();
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println();
    }
}
