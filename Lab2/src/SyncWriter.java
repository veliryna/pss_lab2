import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;

public class SyncWriter {
    String filename;
    FileWriter writer;
    ReentrantLock lock = new ReentrantLock();
    boolean append;

    public SyncWriter(String filename) {
        this.filename = filename;
        this.append = true;
    }

    public void write(String text) {
        lock.lock();
        try {
            writer = new FileWriter(filename, append);
            writer.write(text);
            writer.close();
        } catch (IOException e) {
            System.out.println("File Writer IO exception");
        } finally {
            lock.unlock();
        }
    }

}