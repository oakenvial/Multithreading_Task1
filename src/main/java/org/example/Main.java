package org.example;

import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        String[] texts = new String[25];
        for (int i = 0; i < texts.length; i++) {
            texts[i] = generateText("aab", 30_000);
        }

        final ExecutorService threadPool = Executors.newFixedThreadPool(8);
        List<Future<Integer>> futureTasks = new ArrayList<>();

        long startTs = System.currentTimeMillis(); // start time
        for (String text : texts) {
            final Future<Integer> task = threadPool.submit(getCallable(text));
            futureTasks.add(task);
        }
        int allTimeMax = 0;
        for (Future<Integer> task : futureTasks) {
            allTimeMax = Math.max(allTimeMax, task.get());
        }
        System.out.println("Max value among all intervals: " + allTimeMax);
        threadPool.shutdown();
        long endTs = System.currentTimeMillis(); // end time

        System.out.println("Time: " + (endTs - startTs) + "ms");
    }

    private static Callable<Integer> getCallable(String text) {
        return () -> {
            int maxSize = 0;
            for (int i = 0; i < text.length(); i++) {
                for (int j = 0; j < text.length(); j++) {
                    if (i >= j) {
                        continue;
                    }
                    boolean bFound = false;
                    for (int k = i; k < j; k++) {
                        if (text.charAt(k) == 'b') {
                            bFound = true;
                            break;
                        }
                    }
                    if (!bFound && maxSize < j - i) {
                        maxSize = j - i;
                    }
                }
            }
            System.out.println(text.substring(0, 100) + " -> " + maxSize);
            return maxSize;
        };
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}