package readwrite;

import java.time.LocalTime;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class ReadWrite {

    public static void main(String[] args) throws Exception {
        Data d = new Data();
        long l1 = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(5);
        for(int i=0; i<5; i++)
            es.submit(new WorkWData(d));


        TimeUnit.SECONDS.sleep(3);
        es.shutdown();
        es.awaitTermination(5000, TimeUnit.MILLISECONDS);
        long l2 = System.currentTimeMillis();
        System.out.println(l2-l1);
    }
}

class WorkWData implements Runnable {
    Data obj;
    WorkWData(Data d) {
        obj=d;
    }
    @Override
    public void run() {
        int n;
        String threadName = Thread.currentThread().getName();
        
        n=obj.read();
        System.out.println("First read in " + threadName + " n=" + n);
        obj.write();
        System.out.println("Write ... ");
        n=obj.read();
        System.out.println("Second read in " + threadName + " n=" + n);
    }
}

class Data {
    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock rl = lock.readLock();
    private Lock wl = lock.writeLock();

    private int count=0;

    int read() {
        try {
            rl.lock();
            int n = count;
            TimeUnit.MILLISECONDS.sleep(400);
            count=n;
        } 
        catch (InterruptedException ex) { }
        finally {
            rl.unlock();
        }
        
        return count;
    }
    void write() {
        try {
            wl.lock();
            int n = count;
            TimeUnit.MILLISECONDS.sleep(400);
            n++;
            count=n;
        } 
        catch (InterruptedException ex) { }
        finally {
            wl.unlock();
        }
    }
}

