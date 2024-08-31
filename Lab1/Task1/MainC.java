import java.io.FileWriter;
import java.io.IOException;

public class MainC {
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
		for (int i = 0; i < threads.length; i++) {
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
        int[] totalThreads = new int[]{1, 2, 4, 8, 16, 32, 64};

        int xIterations = 4;
        for(int i = 0; i < totalThreads.length; i++){
            x = 0;
            for (int j = 0; j < xIterations; j++) {
                x = 0;
                run_experiments(totalThreads[i]);
            }
        }
        

        int yIterations = 4;
        double[][] results = new double[totalThreads.length][yIterations];
        for(int i = 0; i < totalThreads.length; i++){
            x = 0;
            for (int j = 0; j < yIterations; j++) {
                x = 0;
                results[i][j] = run_experiments(totalThreads[i]) / 1_000_000_000.0;
                //System.out.println( totalThreads[i] + ",: " + run_experiments(totalThreads[i])/ 1_000_000_000.0);
            }
        }
        
        try {
            FileWriter myWriter = new FileWriter("filename.txt"); 
            for (int i = 0; i < results.length; i++) {
                for (int j = 0; j < results[i].length; j++) {
                    myWriter.write( totalThreads[i] + ", " + String.valueOf(results[i][j]) + "\n");
                } 
            }
            myWriter.close();
              System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
              System.out.println("An error occurred.");
              e.printStackTrace();
        }
    

        //long result = run_experiments(4);
        System.out.println("Final x: " + x);
        //System.out.println("Elapsed time: " + result + " nanoseconds"); // Print the elapsed time
    }
}

