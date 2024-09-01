public class Main {
    public static class Producer implements Runnable {
        private final Buffer buffer;
        public Producer(Buffer buffer) {
            this.buffer = buffer;
        }

        public void run() {
            int size = 1000000;
            for(int i = 0; i < size; i++ ){
                try {
                    buffer.add(i);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                buffer.close();
                Thread.currentThread().interrupt();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class Consumer implements Runnable {
        private final Buffer buffer;

        public Consumer(Buffer buffer) {
            this.buffer = buffer;
        }

        public void run() {
            try {
                while(true) {
                    int k = buffer.remove();
                    System.out.println(k);
                }
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            } catch (IllegalStateException e){
                System.out.println("Consumed");
            }
        }
    }

    public static void main(String [] args) {
        int n = 100;
        Buffer buffer = new Buffer(n);
        Thread tp = new Thread(new Producer(buffer));
        Thread tc = new Thread(new Consumer(buffer));
        tp.start();
        tc.start();
        
        try {
            tp.join();
            tc.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
