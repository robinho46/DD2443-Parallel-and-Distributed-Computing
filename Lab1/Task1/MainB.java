public class MainB {
    static volatile int x;
    private static Object Lock = new Object();

    public static class Incrementer implements Runnable {  
        public void run() { 
            for (int i = 0; i < 1000000; i++) {
                synchronized(Lock){
                    x += 1;
                }
            }
        }
    }

    static long run_experiments(int n) {
        Thread[] threads = new Thread[n];
        long startTime = System.nanoTime();
        for (int i = 0; i < n; i++) {
            threads[i] = new Thread(new Incrementer());
            threads[i].start();
        }

        for(int i = 0; i < threads.length; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public static void main(String [] args) {
        long result = run_experiments(4);
        System.out.println("Final x: " + x);
        System.out.println("Elapsed time: " + result + " nanoseconds"); // Print the elapsed time
    }
}
