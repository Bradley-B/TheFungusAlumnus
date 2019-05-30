import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MessageQueue {

    public static final int CAPACITY = 5;
    private BlockingQueue<String> queue;

    public MessageQueue() {
        this.queue = new ArrayBlockingQueue<String>(CAPACITY);
        for(int i=0;i<CAPACITY;i++) {
            queue.add("");
        }
    }

    public void add(String item) {
        try {
            queue.take();
            queue.add(item);
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    public boolean contains(String item) {
        return queue.contains(item);
    }

}
