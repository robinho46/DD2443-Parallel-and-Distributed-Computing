public class Main4 {
    public static class Runner implements Runnable {
        private final CountingSemaphore semaphore;
        public Runner(CountingSemaphore semaphore) {
            this.semaphore = semaphore;
        }

        public void run() {
            try{
                synchronized (semaphore) {
                    if (semaphore.permit <= 0){
                        System.out.println("Thread id " + Thread.currentThread().getId() + " must wait no resources are left.");
                    }
                    semaphore.s_wait();
                    System.out.println("Thread id " + Thread.currentThread().getId() + " took 1 resource out of " + (semaphore.permit + 1) + " left");
                }

                Thread.sleep(10000); // should be removed to demonstrate no deadlocks
                semaphore.signal(); // should be removed to demonstrate deadlock where it should be deadlock

            }catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String [] args) throws InterruptedException {
        CountingSemaphore semaphore = new CountingSemaphore(3);
        Thread[] threads = new Thread[5];

        for(int i = 0; i < 5; i++){
            threads[i] = new Thread(new Runner(semaphore));
            threads[i].start();
        }

        for(int i = 0; i < 5; i++){
            try {
                threads[i].join();
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
}
