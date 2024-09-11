public class SequentialSort implements Sorter {

        public SequentialSort() {}
    
        @Override
        public void sort(int[] arr) {
                if (arr.length <= 1) {
                    return; // Base case: if array has 1 or 0 elements, it's already sorted
                }
                int mid = arr.length / 2;

                int[] left = new int[mid];
                int[] right = new int[arr.length - mid];
                for (int i = 0; i < mid; i++) {
                    left[i] = arr[i]; 
                }
                for (int i = mid; i < arr.length; i++) {
                    right[i - mid] = arr[i];
                }

                sort(left);
                sort(right);
                mergeSort(left, right, arr);
        }

        public void mergeSort(int[] left, int[] right, int[] arr){
                int i = 0, j = 0, k = 0;
                while (i < left.length && right.length > j) {
                    if(left[i] <= right[j]){
                        arr[k++] = left[i++];
                    } else{
                        arr[k++] = right[j++];
                    }
                }

                while (left.length > i) {
                    arr[k++] = left[i++];
                }
                while (right.length > j) {
                    arr[k++] = right[j++];
                }
        }
	
	@Override
        public int getThreads() {
            return 1; 
        }
}
