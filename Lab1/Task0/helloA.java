import java.lang.Thread;

class helloA extends Thread {

    @Override
    public void run(){
        System.out.println("Hello World");
    }

    public static void main(String[] args){
        helloA thread = new helloA();

        thread.start();
        
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
