import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class Log {

    private Log() {
        // Do not implement
    }

    public static int validate(Log.Entry[] log) {
        Arrays.sort(log, (e1, e2) -> Long.compare(e1.timestamp, e2.timestamp));

        Set<Integer> hashSet = new HashSet<>();
        int discrepancyCount = 0;

        for (Log.Entry entry : log) {
            boolean expectedReturn;

            switch (entry.method) {
                case ADD:
                    expectedReturn = hashSet.add(entry.arg);
                    if (expectedReturn != entry.ret) {
                        discrepancyCount++;
                        System.out.println("Discrepancy found in add: arg=" + entry.arg +
                                           ", expected=" + expectedReturn +
                                           ", actual=" + entry.ret +
                                           ", timestamp=" + entry.timestamp);
                    }
                    break;

                case REMOVE:
                    expectedReturn = hashSet.remove(entry.arg);
                    if (expectedReturn != entry.ret) {
                        discrepancyCount++;
                        System.out.println("Discrepancy found in remove: arg=" + entry.arg +
                                           ", expected=" + expectedReturn +
                                           ", actual=" + entry.ret +
                                           ", timestamp=" + entry.timestamp);
                    }
                    break;

                case CONTAINS:
                    expectedReturn = hashSet.contains(entry.arg);
                    if (expectedReturn != entry.ret) {
                        discrepancyCount++;
                        System.out.println("Discrepancy in contain: arg=" + entry.arg +
                                           ", expected=" + expectedReturn +
                                           ", actual=" + entry.ret +
                                           ", timestamp=" + entry.timestamp);
                    }
                    break;

                default:
                    break;
            }
        }

        return discrepancyCount;
    }

    public static class Entry {
        public Method method;
        public int arg;
        public boolean ret;
        public long timestamp;

        public Entry(Method method, int arg, boolean ret, long timestamp) {
            this.method = method;
            this.arg = arg;
            this.ret = ret;
            this.timestamp = timestamp;
        }
    }

    public enum Method {
        ADD, REMOVE, CONTAINS
    }
}