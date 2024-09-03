import java.lang.Thread;
public class MainA {

    static volatile int x;
    public static class Incrementer implements Runnable {       
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                x += 1;
            }   
        }
    }

    public static void main(String [] args) {
        int numOfThreads = 4;
        Thread[] threads = new Thread[numOfThreads];
        for (int i = 0; i < numOfThreads; i++) {
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
        System.out.println("Final x: " + x);
    }
}
