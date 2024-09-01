public class Main2C {
	private static int sharedInt = 0;
    private static Object lock = new Object();
    
    public static class Incrementer implements Runnable {
		public void run() {
            for (int i = 0; i < 1000000; i++) {
                sharedInt += 1;
            }
            synchronized(lock){
                lock.notify();
            }   
        }
	}

	public static class Printer implements Runnable {
		public void run() {
          synchronized(lock){
                try {
                    while(sharedInt < 1000000){
                       lock.wait();
                    }
                    
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Shared Int: " + sharedInt);
        }
	}

	public static void main(String [] args) {
        Thread t1 = new Thread(new Incrementer());
        Thread t2 = new Thread(new Printer()); 
        t1.start();
        t2.start();
 
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
	}
}
