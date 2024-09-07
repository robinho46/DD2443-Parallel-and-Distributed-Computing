public class CountingSemaphore {
    public int permit = 0;
    public CountingSemaphore(int n) {
        this.permit = n;
    }

    public void signal() {
        // increases the permit, meaning a thread left the resource and +1 is available
        synchronized(this){
            permit += 1;
            // System.out.println("Available resources: "+ permit); uncomment to check the available reosurces 
            notify();
        }
    }

    public void s_wait() throws InterruptedException {
        // decreases the permit, meaning a thread is waiting for a resource and -1 booked
        synchronized(this){
            while(permit <= 0){ // handling spurious wakeup
                wait();
            }
            permit -= 1;
        }
    }
}
