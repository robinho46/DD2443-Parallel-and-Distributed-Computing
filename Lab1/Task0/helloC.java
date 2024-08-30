import java.lang.String;
import java.lang.Thread;

class helloC extends Thread {

    private final long threadNumber;
   
    public helloC (long threadNum){
        this.threadNumber = threadNum;
    }
   
    @Override
    public void run(){
        System.out.println("Hello world I am thread number " + threadNumber + " and my ID is: " + this.getId());
    }

    public static void main(String[] args){
        int cpuNumThreads = Runtime.getRuntime().availableProcessors(); // Get the number of CPU cores
        System.out.println("Available number of threads: " + cpuNumThreads);         
        
        helloC[] threads = new helloC[5];        
        for(int i = 0; i < 5; i++){
            threads[i] = new helloC(i);
            threads[i].start();
            //System.out.println("Robin testar: " + threads[i].threadId());
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

