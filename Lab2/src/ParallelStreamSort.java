
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

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

public class ParallelStreamSort implements Sorter {
	public final int threads;

	public ParallelStreamSort(int threads) {
		this.threads = threads;
	}

	public void sort(int[] arr) {
		if(arr.length <= 1){
			return;
		}

		ForkJoinPool customThreadPool = new ForkJoinPool(threads);
		customThreadPool.submit(() -> mergeSort(arr, 0, arr.length - 1)).join();
	}

	public int getThreads() {
		return threads;
	}

	private void mergeSort(int[] arr, int left, int right) {
		if (left < right) {
			int middle = (left + right) / 2;

			RecursiveAction leftSort = new RecursiveAction() {
				@Override
				protected void compute() {
					mergeSort(arr, left, middle);
				}
			};

			RecursiveAction rightSort = new RecursiveAction() {
				@Override
				protected void compute() {
					mergeSort(arr, middle + 1, right);
				}
			};
			ForkJoinTask.invokeAll(leftSort, rightSort); //handles the parralel execution of the two leftSort and RightSort recursive actiontask.
			merge(arr, left, middle, right);
		}
	}

	public static void merge(int[] arr, int left, int mid, int right) {
		int n1 = mid - left + 1;
	        int n2 = right - mid;

		int[] leftArray = new int[n1];
	        int[] rightArray = new int[n2];

	        // Copy data to temp arrays leftArray[] and rightArray[]
	        for (int i = 0; i < n1; i++) {
			leftArray[i] = arr[left + i];
		}
		for (int i = 0; i < n2; i++) {
			rightArray[i] = arr[mid + 1 + i];
		}

		// Merge the temp arrays
		int i = 0, j = 0, k = left;
		while (i < n1 && j < n2) {
			if (leftArray[i] <= rightArray[j]) {
				arr[k] = leftArray[i];
				i++;
			} else {
				arr[k] = rightArray[j];
				j++;
			}
			k++;
		}

		// Copy remaining elements of leftArray[], if any
		while (i < n1) {
			arr[k] = leftArray[i];
			i++;
			k++;
		}

		// Copy remaining elements of rightArray[], if any
		while (j < n2) {
			arr[k] = rightArray[j];
			j++;
			k++;
		}
	}
}
