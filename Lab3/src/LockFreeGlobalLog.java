import java.util.List;

public class LockFreeGlobalLog {
    private LockFreeQueue<Log.Entry> logQueue;

    public LockFreeGlobalLog() {
        this.logQueue = new LockFreeQueue<>();
    }

    public void addLogEntry(Log.Entry entry) {
        logQueue.enq(entry);
    }

    public List<Log.Entry> getLogEntries() {
        return logQueue.getAllElements();
    }

    public void clear() {
        this.logQueue = new LockFreeQueue<>();
    }
}
