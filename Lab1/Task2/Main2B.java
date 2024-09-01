public class Main2B {
	private static  int sharedInt = 0;
    private static volatile boolean done = false;

    public static class Incrementer implements Runnable {
		public void run() {
            for (int i = 0; i < 1000000; i++) {
                sharedInt += 1;
            }
            done = true;
        }
	}

	public static class Printer implements Runnable {
		public void run() {
	        while(!done){}
           
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
