package servers.UDP;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by n_buga on 01.06.16.
 */
public class NewQueryNewThread extends UDPServer {
    final private int PORT = 9987;

    public NewQueryNewThread() {
        super();
    }

    @Override
    public int getPort() {
        return PORT;
    }

    @Override
    protected void handlerQuery(int remotePort, String remoteAddress, List<Integer> arrayForSorted,
                                long beginQueryHandler) {
        (new Thread(() -> countAndSend(remotePort, remoteAddress, arrayForSorted, beginQueryHandler))).start();
    }

}
