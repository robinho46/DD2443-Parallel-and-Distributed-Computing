import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer {
    private final Deque<Integer> buffer;
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition full = lock.newCondition();
    private final Condition empty = lock.newCondition();
    private boolean close = false;

    public Buffer(int n) {
        buffer = new LinkedList<>();
        this.capacity = n;
    }

    void add(int i) throws InterruptedException {
        lock.lock();
        try {
            while(buffer.size() >= capacity){
                if(close){
                    throw new IllegalStateException("Buffer is closed");
                }
                full.await(); // waits and releases lock
                }

            if(close){
                throw new IllegalStateException("Buffer is closed");
            }
            buffer.addLast(i); // when not full add
            empty.signal(); // signal consumers
            } finally {
            lock.unlock(); // unlocks for handling any exception
            }
    }

    public int remove() throws InterruptedException {
        lock.lock();
        try {
            while(buffer.isEmpty()){
                if(close){
                    throw new IllegalStateException("Buffer is closed and empty");
                }
                empty.await();
            }
            int a = buffer.removeFirst();
            full.signal();
            return a;
        } finally {
            lock.unlock();
        }
    }

    public void close() throws InterruptedException {
        lock.lock();
        try {
            if(!buffer.isEmpty()){
                if(close){
                    throw new IllegalStateException("Buffer is closed");
                }
                close = true;
                full.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }
}
