package edu.neu.coe.info6205.sort;

import edu.neu.coe.info6205.sort.elementary.InsertionSort;
import edu.neu.coe.info6205.util.*;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;


public class InsertionSortBenchmarkTest {

    @Before
    public void setConfig() throws IOException {
        config = Config.load(Benchmarks.class);
    }

    @Test
    public void insertionBenchmark() {
        /**
         * N start from 1000, and doubled 5 times(according to the assignment, it should be 5 at least).
         * Aka 1000, 2000, 4000, 8000, 16000.
         */
        doublingInsertionBenchmark(1000, 5);
    }

    public void doublingInsertionBenchmark(int N, int doubleTimes) {
        for (int i = 0; i < doubleTimes; i++) {
            int TrueN = (int)Math.pow(2, i) * N;
            sort0Test(TrueN);
        }
        for (int i = 0; i < doubleTimes; i++) {
            int TrueN = (int)Math.pow(2, i) * N;
            sort1Test(TrueN);
        }
        for (int i = 0; i < doubleTimes; i++) {
            int TrueN = (int)Math.pow(2, i) * N;
            sort2Test(TrueN);
        }
        for (int i = 0; i < doubleTimes; i++) {
            int TrueN = (int)Math.pow(2, i) * N;
            sort3Test(TrueN);
        }
    }

    // sorted integers
    public void sort0Test(int N) {
        String description = "Insertion sort ordered integers";
        Helper<Integer> helper = new BaseHelper<>(description, N, config);
        final GenericSort<Integer> sort = new InsertionSort<>(helper);
        runOrderedBenchmark(description, sort, helper, N);
    }

    // reverse-ordered integers
    public void sort1Test(int N) {
        String description = "Insertion sort reverse-ordered integers";
        Helper<Integer> helper = new BaseHelper<>(description, N, config);
        final GenericSort<Integer> sort = new InsertionSort<>(helper);
        runReverseOrderedBenchmark(description, sort, helper, N);
    }

    // random order integers
    public void sort2Test(int N) {
        String description = "Insertion sort random order integers";
        Helper<Integer> helper = new BaseHelper<>(description, N, config);
        final GenericSort<Integer> sort = new InsertionSort<>(helper);
        runRandomBenchmark(description, sort, helper, N);
    }

    // partially-ordered integers
    public void sort3Test(int N) {
        String description = "Insertion sort partially-ordered integers";
        Helper<Integer> helper = new BaseHelper<>(description, N, config);
        final GenericSort<Integer> sort = new InsertionSort<>(helper);
        runPartiallyOrderedBenchmark(description, sort, helper, N);
    }

    public void runOrderedBenchmark(String description, GenericSort<Integer> sort, Helper<Integer> helper, int N) {
        helper.init(N);
        Supplier<Integer[]> supplier = () -> generateOrderedIntegers(N);
        final Benchmark<Integer[]> benchmark = new Benchmark_Timer<>(
                description + " for " + N + " Integers",
                (xs) -> Arrays.copyOf(xs, xs.length),
                sort::mutatingSort,
                null
        );
        logger.info(Utilities.formatDecimal3Places(benchmark.runFromSupplier(supplier, M)) + " ms");
    }

    public void runReverseOrderedBenchmark(String description, GenericSort<Integer> sort, Helper<Integer> helper, int N) {
        helper.init(N);
        Supplier<Integer[]> supplier = () -> generateReverseOrderedIntegers(N);
        final Benchmark<Integer[]> benchmark = new Benchmark_Timer<>(
                description + " for " + N + " Integers",
                (xs) -> Arrays.copyOf(xs, xs.length),
                sort::mutatingSort,
                null
        );
        logger.info(Utilities.formatDecimal3Places(benchmark.runFromSupplier(supplier, M)) + " ms");
    }

    public void runRandomBenchmark(String description, GenericSort<Integer> sort, Helper<Integer> helper, int N) {
        helper.init(N);
        Supplier<Integer[]> supplier = () -> helper.random(Integer.class, Random::nextInt);
        final Benchmark<Integer[]> benchmark = new Benchmark_Timer<>(
                description + " for " + N + " Integers",
                (xs) -> Arrays.copyOf(xs, xs.length),
                sort::mutatingSort,
                null
        );
        logger.info(Utilities.formatDecimal3Places(benchmark.runFromSupplier(supplier, M)) + " ms");
    }

    public void runPartiallyOrderedBenchmark(String description, GenericSort<Integer> sort, Helper<Integer> helper, int N) {
        helper.init(N);
        Supplier<Integer[]> supplier = () -> generatePartiallyOrderedIntegers(N);
        final Benchmark<Integer[]> benchmark = new Benchmark_Timer<>(
                description + " for " + N + " Integers",
                (xs) -> Arrays.copyOf(xs, xs.length),
                sort::mutatingSort,
                null
        );
        logger.info(Utilities.formatDecimal3Places(benchmark.runFromSupplier(supplier, M)) + " ms");
    }

    /**
     * generate ordered integers
     *
     * @param N the number of times the function f will be called.
     * @return ordered integers array
     */
    public Integer[] generateOrderedIntegers(int N) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            list.add(i);
        }

        return list.toArray(new Integer[0]);
    }

    /**
     * generate reverse-ordered integers
     *
     * @param N the number of times the function f will be called.
     * @return reverse-ordered integers array
     */
    public Integer[] generateReverseOrderedIntegers(int N) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < N; i++) {
            list.add(N-i-1);
        }

        return list.toArray(new Integer[0]);
    }

    /**
     * generate partially-ordered integers
     * @param N the number of times the function f will be called.
     * @return partially-ordered integers array
     */
    public Integer[] generatePartiallyOrderedIntegers(int N) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < N/2; i++) {
            list.add(i);
        }

        Random random = new Random();
        for (int i = N/2; i < N; i++) {
            list.add(random.nextInt(N+N/2));
        }

        return list.toArray(new Integer[0]);
    }

    final static LazyLogger logger = new LazyLogger(Benchmarks.class);

    private static Config config;

    final static int M = 100;  // the number of times the function f will be called.
}
