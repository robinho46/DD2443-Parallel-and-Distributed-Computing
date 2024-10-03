/**
 * Sort using Java's Thread, Runnable, start(), and join().
 */

public class ThreadSort implements Sorter {
	private final int maxThreads;
	private static final int THRESHOLD = 32;

	public ThreadSort(int maxThreads) {
		this.maxThreads = maxThreads;
	}

	@Override
	public void sort(int[] arr) {
		if (arr == null || arr.length == 0) return;
		parallelMergeSort(arr, 0, arr.length - 1, maxThreads);
	}

	@Override
	public int getThreads() {
		return maxThreads;
	}

	private void parallelMergeSort(int[] arr, int left, int right, int availableThreads) {
		if (left < right) {
			if (right - left + 1 <= THRESHOLD) {
				mergeSort(arr, left, right);
				return;
			}

			int middle = (left + right) / 2;

			if (availableThreads > 1) {
				int threadsForLeft = availableThreads / 2;
				int threadsForRight = availableThreads - threadsForLeft;

				Thread leftWorker = new Thread(() -> parallelMergeSort(arr, left, middle, threadsForLeft));
				Thread rightWorker = new Thread(() -> parallelMergeSort(arr, middle + 1, right, threadsForRight));

				leftWorker.start();
				rightWorker.start();

				try {
					leftWorker.join();
					rightWorker.join();
		                } catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			} else {
				mergeSort(arr, left, middle);
				mergeSort(arr, middle + 1, right);
			}

			merge(arr, left, middle, right);
		}
	}

	private void mergeSort(int[] arr, int left, int right) {
		if (left < right) {
			int middle = (left + right) / 2;
			mergeSort(arr, left, middle);
			mergeSort(arr, middle + 1, right);
			merge(arr, left, middle, right);
		}
	}

	private void merge(int[] arr, int left, int middle, int right) {
		int n1 = middle - left + 1;
		int n2 = right - middle;

		int[] leftArr = new int[n1];
		int[] rightArr = new int[n2];

		System.arraycopy(arr, left, leftArr, 0, n1);
		System.arraycopy(arr, middle + 1, rightArr, 0, n2);

		int i = 0, j = 0, k = left;

		while (i < n1 && j < n2) {
			if (leftArr[i] <= rightArr[j]) {
				arr[k++] = leftArr[i++];
			} else {
				arr[k++] = rightArr[j++];
			}
		}

		while (i < n1) {
			arr[k++] = leftArr[i++];
		}

		while (j < n2) {
			arr[k++] = rightArr[j++];
		}
	}
}
