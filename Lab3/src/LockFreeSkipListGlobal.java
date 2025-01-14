import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeSkipListGlobal<T extends Comparable<T>> implements LockFreeSet<T> {
    /* Number of levels */
    private static final int MAX_LEVEL = 16;

    private final Node<T> head = new Node<>();
    private final Node<T> tail = new Node<>();
    private final LockFreeGlobalLog log = new LockFreeGlobalLog();

    public LockFreeSkipListGlobal() {
        for (int i = 0; i <= MAX_LEVEL; i++) {
            head.next[i].set(tail, false);
        }
    }

    private static final class Node<T> {
        private final T value;
        private final AtomicMarkableReference<Node<T>>[] next;
        private final int topLevel;

        @SuppressWarnings("unchecked")
        public Node() {
            value = null;
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[MAX_LEVEL+1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<>(null, false);
            }
            topLevel = MAX_LEVEL;
        }

        @SuppressWarnings("unchecked")
        public Node(T x, int height) {
            value = x;
            next = (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[height+1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<>(null, false);
            }
            topLevel = height;
        }
    }

    /* Returns a random level between 0 and MAX_LEVEL,
     * with probability P(randomLevel() = x) = 1/2^(x+1) for x < MAX_LEVEL.
     */
    private static int randomLevel() {
        int r = ThreadLocalRandom.current().nextInt();
        int level = 0;
        r &= (1 << MAX_LEVEL) - 1;
        while ((r & 1) != 0) {
            r >>>= 1;
            level++;
        }
        return level;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean add(int threadId, T x) {
        int topLevel = randomLevel();
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL+1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL+1];
        while (true) {
            Object[] result = find(x, preds, succs);
            boolean found = (boolean) result[0];
            long time = (long) result[1];
            if (found) {
                log.addLogEntry(new Log.Entry(Log.Method.ADD, x.hashCode(), false, time));
                return false;
            } else {
                Node<T> newNode = new Node<>(x, topLevel);
                for (int level = bottomLevel; level <= topLevel; level++) {
                    newNode.next[level].set(succs[level], false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode, false, false)) {
                    continue;
                }
                time = System.nanoTime(); // linearization point for a successful add
                for (int level = bottomLevel+1; level <= topLevel; level++) {
                    while (true) {
                        pred = preds[level];
                        succ = succs[level];
                        if (pred.next[level].compareAndSet(succ, newNode, false, false)) {
                            break;
                        }
                        find(x, preds, succs);
                    }
                }
                log.addLogEntry(new Log.Entry(Log.Method.ADD, x.hashCode(), true, time));
                return true;
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean remove(int threadId, T x) {
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL+1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL+1];
        Node<T> succ;
        while (true) {
            Object[] result = find(x, preds, succs);
            boolean found = (boolean) result[0];
            long time = (long) result[1];
            if (!found) {
                log.addLogEntry(new Log.Entry(Log.Method.REMOVE, x.hashCode(), false, time));
                return false;
            } else {
                Node<T> nodeToRemove = succs[bottomLevel];
                for (int level = nodeToRemove.topLevel; level >= bottomLevel + 1; level--) {
                    boolean[] marked = {false};
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].compareAndSet(succ, succ, false, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                }
                boolean[] marked = {false};
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ, false, true);
                    time = System.nanoTime(); // linearization point for removal
                    if (iMarkedIt) {
                        find(x, preds, succs);
                        log.addLogEntry(new Log.Entry(Log.Method.REMOVE, x.hashCode(), true, time));
                        return true;
                    } else {
                        succ = nodeToRemove.next[bottomLevel].get(marked);
                        if (marked[0]) {
                            log.addLogEntry(new Log.Entry(Log.Method.REMOVE, x.hashCode(), false, time));
                            return false;
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean contains(int threadId, T x) {
        int bottomLevel = 0;
        boolean[] marked = {false};
        Node<T> pred = head;
        Node<T> curr = null;
        Node<T> succ = null;
        long linearizationTime;
        for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
            curr = pred.next[level].getReference();
            linearizationTime = System.nanoTime();
            while (true) {
                succ = curr.next[level].get(marked);
                while (marked[0]) {
                    curr = succ;
                    linearizationTime = System.nanoTime();
                    succ = curr.next[level].get(marked);
                }
                if (curr.value != null && x.compareTo(curr.value) > 0) {
                    pred = curr;
                    curr = succ;
                } else {
                    break;
                }
            }
        }
        linearizationTime = System.nanoTime();
        boolean result = (curr.value != null && x.compareTo(curr.value) == 0);
        log.addLogEntry(new Log.Entry(Log.Method.CONTAINS, x.hashCode(), result, linearizationTime));
        return result;
    }

    @SuppressWarnings("unchecked")
    private Object[] find(T x, Node<T>[] preds, Node<T>[] succs) {
        int bottomLevel = 0;
        boolean[] marked = {false};
        boolean snip;
        Node<T> pred = null;
        Node<T> curr = null;
        Node<T> succ = null;
        long linearizationTime = System.nanoTime();
        boolean result = false;
        retry:
        while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                linearizationTime = System.nanoTime();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {
                        snip = pred.next[level].compareAndSet(curr, succ, false, false);
                        if (!snip)
                            continue retry;
                        linearizationTime = System.nanoTime();
                        curr = succ;
                        succ = curr.next[level].get(marked);
                    }
                    if (curr.value != null && x.compareTo(curr.value) > 0) {
                        pred = curr;
                        curr = succ;
                    } else {
                        break;
                    }
                }
                preds[level] = pred;
                succs[level] = curr;
            }
            if (curr.value != null && x.compareTo(curr.value) == 0) {
                result = true;
            }
            return new Object[] {result, linearizationTime};
        }
    }

    @Override
    public Log.Entry[] getLog() {
        List<Log.Entry> entries = log.getLogEntries(); // take a snapshot (does not drain)
        entries.sort((e1, e2) -> Long.compare(e1.timestamp, e2.timestamp));
        return entries.toArray(new Log.Entry[0]);
    }

    @Override
    public void reset() {
        for (int i = 0; i <= MAX_LEVEL; i++) {
            head.next[i].set(tail, false);
        }
        log.clear();
    }
}