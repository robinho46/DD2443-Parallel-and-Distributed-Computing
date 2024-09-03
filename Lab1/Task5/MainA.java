import java.util.concurrent.locks.Lock;

public class MainA {
    
	public static class Philosopher implements Runnable {
        private Object leftFork = new Object();
        private Object rightFork = new Object();
        private int id;
        public Philosopher(Object leftFork, Object rightFork, int id) {
            this.leftFork = leftFork;
            this.rightFork = rightFork;
            this.id = id;
        }
        
        public void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking");
            Thread.sleep(10);    
        }

        public void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is eating");
            Thread.sleep(10);
        }
                
        @Override
        public void run() {
            try {
                while (true) {
                    think();
                    synchronized (leftFork) {
                        System.out.println("Philosopher " + id + " picked up left fork");
                        synchronized (rightFork) {
                            System.out.println("Philosopher " + id + " picked up right fork");
                            eat();
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
        /*
     * Each philosopher has their fork to the right
     * 
     * Breakdown:
     * To access the right fork on the right you just take their id on the forks ids
     * To access the left fork we we know the right fork since it is the same as their id, we can than back trace to the number before to get the left fork.
     * 
     * EDGE CASE: 
     * For philosopher 0 he will have the 0 for right fork but thinking of left fork we have to go to the end of numbers which is 4.
     */
    
    public static void main(String [] args) {
        Philosopher[] philosophers = new Philosopher[5];
        Object[] forks = new Object[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new Object();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Object rightFork = forks[i];
            Object leftFork;
            if(i > 0){
                leftFork = forks[i - 1];
            } else{
                leftFork = forks[philosophers.length - 1];
            }
            philosophers[i] = new Philosopher(leftFork, rightFork, i);
            
            Thread t = new Thread(philosophers[i]);
            t.start();
        }
    }    
}
