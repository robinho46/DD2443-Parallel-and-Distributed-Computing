import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinPoolSort implements Sorter {
        public final int threads;
        private final ForkJoinPool pool;

        public ForkJoinPoolSort(int threads) {
                this.threads = threads;
                pool = new ForkJoinPool(threads);
        }

        public void sort(int[] arr) {
                pool.invoke(new Worker(arr, 0, arr.length));
        }

        public int getThreads() {
                return threads;
        }

        private static class Worker extends RecursiveAction {
                private final int[] arr;
                private final int left, right;

                public Worker(int[] arr, int left, int right) {
                        this.arr = arr;
                        this.left = left;
                        this.right = right;
                }
                protected void compute() {
                        if (right - left <= 1) {
                                return;
                        }

                        int mid = (left + right) / 2;
                        Worker leftTask = new Worker(arr, left, mid);
                        Worker rightTask = new Worker(arr, mid, right);

                        // fork tasks
                        leftTask.fork();
                        rightTask.fork();

                        // join tasks
                        leftTask.join();
                        rightTask.join();

                        merge(arr, left, mid, right);

                }
                private void merge(int[] arr, int left, int mid, int right) {
                        int[] temp = new int[right - left];
                        int i = left, j = mid, k = 0;

                        while (i < mid && j < right) {
                                if (arr[i] <= arr[j]) {
                                        temp[k++] = arr[i++];
                                } else {
                                        temp[k++] = arr[j++];
                                }
                        }
                        while (i < mid) {
                                temp[k++] = arr[i++];
                        }
                        while (j < right) {
                                temp[k++] = arr[j++];
                        }
                        System.arraycopy(temp, 0, arr, left, temp.length);
                }
        }
}
