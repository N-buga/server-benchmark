package servers.TCP;

import servers.UDP.NewQueryNewThread;
import utils.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by n_buga on 31.05.16.
 */
public class NewQueryNewConnection extends SocketIOServer {
    final private int PORT = 9991;

    public NewQueryNewConnection() {
        super();
    }

    @Override
    protected void handlerConnection() {
        try (ServerSocket serverSocket = new ServerSocket(PORT, 100)){
            serverSocket.setSoTimeout(TIMEOUT);
            while (!end) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                Utils.Connection connection = new Utils.Connection(socket);
                oneQueryHandler(connection);
                connection.close();
                try {
                    Thread.sleep(TIMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        end = true;
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getPort() {
        return PORT;
    }
}
