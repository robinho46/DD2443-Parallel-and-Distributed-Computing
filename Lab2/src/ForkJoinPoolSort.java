import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ForkJoinPoolSort implements Sorter {
	private final ForkJoinPool pool;
	private final int threads;

	private static final int SORT_THRESHOLD = 16;

	public ForkJoinPoolSort(int threads) {
		this.pool = new ForkJoinPool(threads);
		this.threads = threads;
	}

	@Override
	public void sort(int[] arr) {
		if (arr == null || arr.length <= 1) return;
		int[] temp = new int[arr.length];
		pool.invoke(new Worker(arr, temp, 0, arr.length - 1));
	}

	@Override
	public int getThreads() {
		return this.threads;
	}

	private static class Worker extends RecursiveAction {
		private final int[] arr, temp;
		private final int left, right;

		public Worker(int[] arr, int[] temp, int left, int right) {
			this.arr = arr;
			this.temp = temp;
			this.left = left;
			this.right = right;
		}

		@Override
		protected void compute() {
			if (right - left + 1 <= SORT_THRESHOLD) {
				sequentialMergeSort(arr, temp, left, right);
				return;
			}

			int mid = (left + right) / 2;
			Worker leftTask = new Worker(arr, temp, left, mid);
			Worker rightTask = new Worker(arr, temp, mid + 1, right);

			invokeAll(leftTask, rightTask);
			merge(arr, temp, left, mid, right);
		}

		private void sequentialMergeSort(int[] arr, int[] temp, int left, int right) {
			if (left >= right) return;

			int mid = (left + right) / 2;
			sequentialMergeSort(arr, temp, left, mid);
			sequentialMergeSort(arr, temp, mid + 1, right);
			merge(arr, temp, left, mid, right);
		}

		private void merge(int[] arr, int[] temp, int left, int mid, int right) {
			System.arraycopy(arr, left, temp, left, right - left + 1);

		        int i = left;
			int j = mid + 1;
			int k = left;

			while (i <= mid && j <= right) {
				if (temp[i] <= temp[j]) {
					arr[k++] = temp[i++];
				} else {
					arr[k++] = temp[j++];
				}
			}

			while (i <= mid) {
				arr[k++] = temp[i++];
			}
		}
	}
}
