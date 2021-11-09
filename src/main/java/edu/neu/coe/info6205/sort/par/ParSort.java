package edu.neu.coe.info6205.sort.par;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

/**
 * This code has been fleshed out by Ziyao Qiao. Thanks very much.
 * TODO tidy it up a bit.
 * Parallel Sort
 */
class ParSort {

    private static int cutoff = 1000;

    // the number of available threads, should be 2^n
    // use common pool as default, which use core number of your pc as thread level
    private static ForkJoinPool myPool = ForkJoinPool.commonPool();

    /**
     *
     * @param array
     * @param from index of the first key
     * @param to length of the part array to be sorted ( = last index + 1)
     */
    public static void sort(int[] array, int from, int to) {
        if (to - from < cutoff) // use system sort when number of elements is less than cutoff
            Arrays.sort(array, from, to);
        else {
            // FIXME next few lines should be removed from public repo.
            int mid = from + (to - from) / 2;
            // sort the first part and the second part
            CompletableFuture<int[]> firstPart = parsort(array, from, mid); // TO IMPLEMENT
            CompletableFuture<int[]> secondPart = parsort(array, mid, to); // TO IMPLEMENT
            // merge two parts
            CompletableFuture<int[]> cf = firstPart.thenCombine(secondPart, ParSort::merge);
            cf.whenComplete((result, throwable) -> System.arraycopy(result, 0, array, from, result.length));
            cf.join();
        }
    }

    private static CompletableFuture<int[]> parsort(int[] array, int from, int to) {
        return CompletableFuture.supplyAsync(
                () -> {
                    int[] result = new int[to - from];
                    // TO IMPLEMENT
                    System.arraycopy(array, from, result, 0, result.length);
                    sort(result, 0, to - from);
                    return result;
                }
        , myPool);
    }

    private static int[] merge(int[] xs1, int[] xs2) {
        int[] result = new int[xs1.length + xs2.length];
        int i=0, j=0;
        for (int k = 0; k < result.length; k++) {
            if (i >= xs1.length) result[k] = xs2[j++];
            else if (j >= xs2.length) result[k] = xs1[i++];
            else if (xs2[j] < xs1[i]) result[k] = xs2[j++];
            else result[k] = xs1[i++];
        }

        return result;
    }

    public static void setCutoff(int cutoff) {
        ParSort.cutoff = cutoff;
    }

    public static void setMyPool(ForkJoinPool myPool) {
        if (ParSort.myPool!=null)
            shutdownPool();
        ParSort.myPool = myPool;
    }

    public static ForkJoinPool getMyPool() {
        return myPool;
    }

    public static int getCutoff() {
        return cutoff;
    }

    public static void shutdownPool() {
        myPool.shutdown();
    }
}