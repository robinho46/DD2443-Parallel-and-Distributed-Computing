import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main5B {
    
	public static class Philosopher implements Runnable {
        private Lock leftFork = new ReentrantLock(true);
        private Lock rightFork = new ReentrantLock(true);
        private int id;
        public Philosopher(Lock leftFork, Lock rightFork, int id) {
            this.leftFork = leftFork;
            this.rightFork = rightFork;
            this.id = id;
        }
        
        public void think() throws InterruptedException {
            System.out.println("Philosopher " + id + " is thinking");
            Thread.sleep(((int) (Math.random() * 100))); // Sleeps for a random amount of time 
        }

        public void eat() throws InterruptedException {
            System.out.println("Philosopher " + id + " is eating");
            Thread.sleep(((int) (Math.random() * 100))); // Sleeps for a random amount of time 
        }

        @Override
        public void run() {
            try {
                while (true) {
                    think();
                    // Check if id is odd or even
                    if (id % 2 == 0) {
                        acquireLocks(leftFork, rightFork); // If id is even then start with the left fork and the right fork
                    } else {
                        acquireLocks(rightFork, leftFork); // If odd then start with right fork then left fork
                    }
                    eat(); // Start eat for a random amount of time
                    leftFork.unlock();
                    rightFork.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void acquireLocks(Lock firstLock, Lock secondLock) throws InterruptedException {
            while (true) {
                boolean gotFirstLock = false;
                boolean gotSecondLock = false;
                // Try to acquire both forks
                try {
                    gotFirstLock = firstLock.tryLock();
                    gotSecondLock = secondLock.tryLock();
                } finally {
                    if (gotFirstLock && gotSecondLock) {
                        return; // Successfully acquired both locks
                    }
                    // Release if only one fork is available
                    if (gotFirstLock) {
                        firstLock.unlock(); 
                    }
                    if (gotSecondLock) {
                        secondLock.unlock();
                    }
                    // Wait a bit before retrying
                    Thread.sleep((int) (Math.random() * 10));
                }
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
        Lock[] forks = new ReentrantLock[philosophers.length];

        for (int i = 0; i < forks.length; i++) {
            forks[i] = new ReentrantLock();
        }

        for (int i = 0; i < philosophers.length; i++) {
            Lock rightFork = forks[i];
            Lock leftFork;
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
