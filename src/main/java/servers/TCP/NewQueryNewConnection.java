package servers.TCP;

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

    @Override
    protected void handlerConnection() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            serverSocket.setSoTimeout(TIMEOUT);
            while (!end) {
                Socket socket;
                try {
                    socket = serverSocket.accept();
                } catch (SocketTimeoutException e) {
                    continue;
                }
                Utils.Connection connection = new Utils.Connection(socket, "", 0);
                oneQueryHandler(connection);
                connection.close();
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
