package edu.neu.coe.info6205.sort.par;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 */
public class Main {

    public static void main(String[] args) {
//        processArgs(args);
        // set thread number to run
        int base=1;
        List<Integer> threadCounts = new ArrayList<>();
        for (int i = 0; i < 4; i++) // 2, 4, 8, 16
            threadCounts.add(base*=2);

        Random random = new Random();

        for (Integer threadCount : threadCounts) {
            // TODO: 1. set the number of available threads
            ParSort.setMyPool(new ForkJoinPool(threadCount));
            System.out.println("Degree of parallelism: " + ParSort.getMyPool().getParallelism());
            List<Long> timeList = new ArrayList<>(); // for same thread but different cutoffs
            List<Integer> cutoffs = new ArrayList<>();
            List<Integer> arraySizes = new ArrayList<>();

            for (int arraySize = 2000000; arraySize <= 10000000; arraySize+=1000000) {
                int halfArraySize = arraySize/2;
                int[] array = new int[arraySize];

                for (int cutoff = 1000; cutoff <= halfArraySize; cutoff+=cutoff) {
                    // TODO: 2. set cutoff, relative to size of the array
                    ParSort.setCutoff(cutoff);
                    long time=0L, startTime, endTime;
                    for (int t = 0; t < 10; t++) {
                        for (int i = 0; i < array.length; i++) array[i] = random.nextInt(10000000);
                        startTime = System.currentTimeMillis();
                        ParSort.sort(array, 0, array.length);
                        endTime = System.currentTimeMillis();
                        time += endTime-startTime;
                    }

                    timeList.add(time/10);
                    arraySizes.add(arraySize);
                    cutoffs.add(cutoff);

                    StringBuilder sb = new StringBuilder();
                    sb.append("arraySize: ").append(arraySize).append(" ,");
                    sb.append("cutoff: ").append(cutoff).append(" ,");
                    sb.append("time/10: ").append(time/10).append(" ms");
                    System.out.println(sb.toString());

                    System.out.println("cutoff：" + (ParSort.getCutoff()) + "\t\t10times Time:" + time + "ms");
                }
            }

            writeToFile(threadCount, timeList, cutoffs, arraySizes);
        }
    }

    private static void writeToFile(int threadCount, List<Long> timeList, List<Integer> cutoffs, List<Integer> arraySizes) {
        System.out.println("Starting to write to file");
        String path = "./src/";
        String fileName = path + threadCount + "threads-result.csv";
        System.out.println("Write to: " + fileName);
        try {
            FileOutputStream fis = new FileOutputStream(fileName);
            OutputStreamWriter isr = new OutputStreamWriter(fis);
            BufferedWriter bw = new BufferedWriter(isr);
            System.out.println(timeList.size());
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < timeList.size(); i++) {
                sb.append(arraySizes.get(i)).append(",");
                sb.append(cutoffs.get(i)).append(",");
                sb.append(timeList.get(i)).append("\n");
                String content = sb.toString();
                bw.write(content);
                bw.flush();
                sb.setLength(0);
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processArgs(String[] args) {
        String[] xs = args;
        while (xs.length > 0)
            if (xs[0].startsWith("-")) xs = processArg(xs);
    }

    private static String[] processArg(String[] xs) {
        String[] result = new String[0];
        System.arraycopy(xs, 2, result, 0, xs.length - 2);
        processCommand(xs[0], xs[1]);
        return result;
    }

    private static void processCommand(String x, String y) {
        if (x.equalsIgnoreCase("N")) setConfig(x, Integer.parseInt(y));
        else
            // TODO sort this out
            if (x.equalsIgnoreCase("P")) //noinspection ResultOfMethodCallIgnored
                ForkJoinPool.getCommonPoolParallelism();
    }

    private static void setConfig(String x, int i) {
        configuration.put(x, i);
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<String, Integer> configuration = new HashMap<>();

    private static boolean isSorted(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i-1]) return false;
        }
        return true;
    }
}
