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
import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.IntStream;

public class ParallelStreamSort implements Sorter {

	private final ForkJoinPool pool;
	private final int threads;
	private final int threshold = 32;
	private final SequentialSort sequentialSort = new SequentialSort();

	public ParallelStreamSort(int threads) {
		this.threads = threads;
		this.pool = new ForkJoinPool(threads);
	}

	@Override
	public void sort(int[] arr) {
		try {
			pool.submit(() -> parallelMergeSort(arr, 0, arr.length - 1)).get();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void parallelMergeSort(int[] arr, int left, int right) {
		if (right - left + 1 <= threshold) {
			sequentialSortInPlace(arr, left, right);
			return;
		}

		if (left < right) {
			int mid = (left + right) / 2;

			pool.submit(() -> 
			IntStream.of(0, 1).parallel().forEach(i -> {
				if (i == 0) {
					parallelMergeSort(arr, left, mid);
				} else {
					parallelMergeSort(arr, mid + 1, right);
				}
			})
			).join();
			merge(arr, left, mid, right);
		}
	}

	private void merge(int[] arr, int left, int mid, int right) {
		int[] leftArray = Arrays.copyOfRange(arr, left, mid + 1);
		int[] rightArray = Arrays.copyOfRange(arr, mid + 1, right + 1);

		int i = 0, j = 0, k = left;

		while (i < leftArray.length && j < rightArray.length) {
			if (leftArray[i] <= rightArray[j]) {
				arr[k++] = leftArray[i++];
			} else {
				arr[k++] = rightArray[j++];
			}
		}

		while (i < leftArray.length) {
			arr[k++] = leftArray[i++];
		}

		while (j < rightArray.length) {
			arr[k++] = rightArray[j++];
		}
	}

	private void sequentialSortInPlace(int[] arr, int left, int right) {
		if (left >= right) {
			return;
		}
		int mid = (left + right) / 2;
		sequentialSortInPlace(arr, left, mid);
		sequentialSortInPlace(arr, mid + 1, right);
		merge(arr, left, mid, right);
	}

	@Override
	public int getThreads() {
		return threads;
	}
}
