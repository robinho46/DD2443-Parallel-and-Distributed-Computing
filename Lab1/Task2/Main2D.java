public class Main2D {

    private static int sharedInt = 0;
    private static volatile boolean done = false;
    private static final Object lock = new Object();
    private static long timeBusy = 0;
    private static long timeGuarded = 0;
    private static final int warmUp = 20;
    private static final int test = 20;

    public static class IncrementerBusy implements Runnable {
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                sharedInt += 1;
            }
            timeBusy = System.nanoTime();
            done = true;
        }
    }
    
    public static class PrinterBusy implements Runnable {
        public void run() {
            while(!done){}
            timeBusy = System.nanoTime() - timeBusy;
        }
    }

    public static class IncrementerGuarded implements Runnable {
        public void run() {
            for (int i = 0; i < 1000000; i++) {
                sharedInt += 1;
            }
            synchronized(lock){
                timeGuarded = System.nanoTime();
                lock.notify();
            }
        }
    }

    public static class PrinterGuarded implements Runnable {
        public void run() {
            synchronized(lock){
                try {
                    while(sharedInt < 1000000) {
                        lock.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            timeGuarded = System.nanoTime() - timeGuarded;
        }
    }
    
    public static void main(String [] args) {
        // TIME BUSY
        for(int i = 0; i < warmUp; i++){
            done = false;
            sharedInt = 0;
            timeBusy = 0;
            Thread t1 = new Thread(new IncrementerBusy());
            Thread t2 = new Thread(new PrinterBusy());
            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        timeBusy = 0;
        for(int i = 0; i < test; i++){
            done = false;
            sharedInt = 0;
            Thread t1 = new Thread(new IncrementerBusy());
            Thread t2 = new Thread(new PrinterBusy());
            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            timeBusy += timeBusy;
        }
        System.out.println("Average time for busy block: " + timeBusy/test);

        // TIME GUARDED
        for(int i = 0; i < warmUp; i++){
            sharedInt = 0;
            timeGuarded = 0;
            Thread t1 = new Thread(new IncrementerGuarded());
            Thread t2= new Thread(new PrinterGuarded());
            t1.start();
            t2.start();

            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // TIME GUARDED MEASUREMENT
        timeGuarded = 0;
        for(int i = 0; i < test; i++){
            sharedInt = 0;
            Thread t1 = new Thread(new IncrementerGuarded());
            Thread t2 = new Thread(new PrinterGuarded());
            t1.start();
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            timeGuarded += timeGuarded;
        }
        System.out.println("Average time for guarded block: " + timeGuarded/test);
    }
}
