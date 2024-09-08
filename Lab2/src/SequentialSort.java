public class SequentialSort implements Sorter {

    public SequentialSort() {
    
    }
    
    @Override
    public int[] sort(int[] arr) {
        if (arr.length <= 1) {
            return arr; // Base case: if array has 1 or 0 elements, it's already sorted
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

        int[] l = sort(left);
        int[] r = sort(right);
        return mergeSort(l, r, arr.length);
    }
    
    public int[] mergeSort(int[] left, int[] right, int length){
        int[] results = new int[length];
        
        int i = 0, j = 0, k = 0;
        while (i < left.length && right.length > j) {
            if(left[i] <= right[j]){
                results[k++] = left[i++];
            } else{
                results[k++] = right[j++];
            }
        }
       
        while (left.length > i) {
            results[k++] = left[i++];
        }
        while (right.length > j) {
            results[k++] = right[j++];
        }
       return results;
    }

    public int getThreads() {
            return 1; 
    }
}
