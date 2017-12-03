package filecatalog.client.view;


public class ThreadSafeStdOut {
    public synchronized void print(String message){
        System.out.print(message);
    }

    public synchronized void println(String message){
        System.out.println(message);
    }
}