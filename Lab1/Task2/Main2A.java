public class Main2A {
    private static int sharedInt;

    public static class Incrementer implements Runnable {
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                sharedInt += 1;
            }   		    
        }
    }

    public static class Printer implements Runnable {
        public void run() {
            System.out.println("Shared Int: " + sharedInt);
        }
    }

    public static void main(String [] args) {
        Thread t1 = new Thread(new Incrementer());
        t1.start();
        Thread t2 = new Thread(new Printer());
        t2.start();
         
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
