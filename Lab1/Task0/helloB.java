import java.lang.String;
import java.lang.Thread;

class helloB extends Thread {
    private final int threadNumber;

    public helloB (int threadNum){
        this.threadNumber = threadNum;
    }
   
    @Override
    public void run(){
        System.out.println("Hello world I am thread number " + threadNumber);
    }

    public static void main(String[] args){
        int numThreads = Runtime.getRuntime().availableProcessors(); // Get the number of CPU cores
        System.out.println("Available number of threads: " + numThreads);         
        
        helloB[] threads = new helloB[5];

        for(int i = 0; i < 5; i++){
            threads[i] = new helloB(i);
            threads[i].start();
        }
        
        for (int i = 0; i < threads.length; i++) {
            try{
                threads[i].join();
            } catch (InterruptedException e){
                e.printStackTrace();
            } 
        }

        System.out.println("Goodbye");

    }
}

