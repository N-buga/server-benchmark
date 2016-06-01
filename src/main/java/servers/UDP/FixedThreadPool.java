package servers.UDP;

import servers.BaseServer;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by n_buga on 01.06.16.
 */
public class FixedThreadPool extends UDPServer{
    final private int PORT = 9984;
    private final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(4);

    public FixedThreadPool() {
        super();
    }

    @Override
    public int getPort() {
        return PORT;
    }

    @Override
    protected void handlerQuery(int remotePort, String remoteAddress, List<Integer> arrayForSorted) {
        EXECUTOR_SERVICE.submit(() -> countAndSend(remotePort, remoteAddress, arrayForSorted));
    }
}
