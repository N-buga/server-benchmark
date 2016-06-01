package servers.NIO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by n_buga on 31.05.16.
 */
public class FixedThreadPool extends NIOThreadPool {
    final private int PORT = 9992;
    final private ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    @Override
    protected ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }

    @Override
    public int getPort() {
        return PORT;
    }
}
