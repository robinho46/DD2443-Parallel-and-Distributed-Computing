import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;

/**
 * Sort using Java's ParallelStreams and Lambda functions.
 *
 * Hints:
 * - Do not take advice from StackOverflow.
 * - Think outside the box.
 *      - Stream of threads?
 *      - Stream of function invocations?
 *
 * By default, the number of threads in parallel stream is limited by the
 * number of cores in the system. You can limit the number of threads used by
 * parallel streams by wrapping it in a ForkJoinPool.
 *      ForkJoinPool myPool = new ForkJoinPool(threads);
 *      myPool.submit(() -> "my parallel stream method / function");
 */

public class ParallelStreamSort implements Sorter {
        private final int threads;
        private final ForkJoinPool pool;

        public ParallelStreamSort(int threads) {
                this.threads = threads;
                this.pool = new ForkJoinPool(threads);
        }
        public void sort(int[] arr) {
                pool.submit(() -> {
                        int[] sortedArr = parallelMergeSort(arr);
                        System.arraycopy(sortedArr, 0, arr, 0, arr.length);
                }).join();
        }
        public int getThreads() {
                return threads;
        }

        private int[] parallelMergeSort(int[] arr) {
                if (arr.length <= 1) {
                        return arr;
                }
                int mid = arr.length / 2;

                int[] left = pool.submit(() -> parallelMergeSort(Arrays.copyOfRange(arr, 0, mid))).join();
                int[] right = pool.submit(() -> parallelMergeSort(Arrays.copyOfRange(arr, mid, arr.length))).join();

                return merge(left, right);
        }

        private int[] merge(int[] left, int[] right) {
                int[] result = new int[left.length + right.length];
                int i = 0, j = 0, k = 0;

                while (i < left.length && j < right.length) {
                        if (left[i] <= right[j]) {
                                result[k++] = left[i++];
                        } else {
                                result[k++] = right[j++];
                        }
                }
                while (i < left.length) {
                        result[k++] = left[i++];
                }

                while (j < right.length) {
                        result[k++] = right[j++];
                }

                return result;
        }
}
