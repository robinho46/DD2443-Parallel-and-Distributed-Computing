public class CountingSemaphore {
    public int permit = 0;
    public CountingSemaphore(int n) {
        this.permit = n;
    }

    public void signal() {
        // increases the permit, meaning a thread left the resource and +1 is available
        synchronized(this){
            permit += 1;
            notify();
        }
    }

    public void s_wait() throws InterruptedException {
        // decreases the permit, meaning a thread is waiting for a resource and -1 booked
        synchronized(this){
            permit -= 1;
            while(permit < 0){ // handling spurious wakeup
                wait();
            }
        }
    }
}