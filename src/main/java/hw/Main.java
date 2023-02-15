package hw;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

public class Main {

    public static final int queueMaxSize = 100;
    public static final String letters = "abc";
    public static final int numberOfTexts = 10_000;
    public static final int lengthOfTexts = 100_000;

    public static ArrayBlockingQueue<String> queueA = new ArrayBlockingQueue<>(queueMaxSize);
    public static ArrayBlockingQueue<String> queueB = new ArrayBlockingQueue<>(queueMaxSize);
    public static ArrayBlockingQueue<String> queueC = new ArrayBlockingQueue<>(queueMaxSize);

    public static void main(String[] args) {

        new Thread(() -> {

            for (int i = 0; i < numberOfTexts; i++) {
                //генерим 10_000 текстов длиной 100_000 символов
                String text = generateText(letters, lengthOfTexts);
                try {
                    //отправляем в очереди
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }).start();

        //стартуем потоки расчётов
        queueThreadStarter(queueA, letters.charAt(0));
        queueThreadStarter(queueB, letters.charAt(1));
        queueThreadStarter(queueC, letters.charAt(2));

    }


    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void queueThreadStarter(ArrayBlockingQueue<String> queue, char ch) {

        new Thread(() -> {
            int maxValue = 0;
            String result = null;
            for (int i = 0; i < numberOfTexts; i++) {
                try {
                    String str = queue.take();
                    int countResult = (int) str.chars()
                            .filter(c -> c == ch)
                            .count();
                    if (countResult > maxValue) {
                        maxValue = countResult;
                        result = str;
                    }

                } catch (InterruptedException e) {
                    return;
                }
            }
            if ((result != null ? result.length() : 0) > 0) {
                System.out.println("Буква '" + ch + "' встретилась " + maxValue + " раз(a) в строке " + result.substring(0, 45) + "..." +
                        result.substring(result.length() - 45));
            }
        }).start();
    }

}