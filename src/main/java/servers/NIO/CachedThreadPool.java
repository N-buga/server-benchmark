package servers.NIO;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by n_buga on 31.05.16.
 */
public class CachedThreadPool extends NIOThreadPool {
    private final int PORT = 9990;
    private final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

    public CachedThreadPool() {
        super();
    }

    @Override
    protected ExecutorService getExecutorService() {
        return EXECUTOR_SERVICE;
    }

    @Override
    public int getPort() {
        return PORT;
    }
}
