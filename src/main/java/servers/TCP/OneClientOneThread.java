package servers.TCP;

import servers.TCP.SocketIOServer;
import utils.Utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by n_buga on 28.05.16.
 */
public class OneClientOneThread extends SocketIOServer {
    private final int PORT = 9999;

    public OneClientOneThread() {
        super();
    }

    @Override
    public void start() {
        Thread server = new Thread(this::handlerConnection);
        server.start();
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
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
                Utils.Connection connection = new Utils.Connection(socket);
                (new Thread(() -> handlerQuery(connection))).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
