package servers;

import servers.NIO.CachedThreadPool;
import servers.NIO.NewQueryNewThread;
import servers.NIO.OneThread;
import servers.TCP.CachedThreadPoolServer;
import servers.TCP.NewQueryNewConnection;
import servers.TCP.OneClientOneThread;
import servers.UDP.FixedThreadPool;

import java.util.Scanner;
import java.util.concurrent.SynchronousQueue;

/**
 * Created by n_buga on 02.06.16.
 */
public class RunAllServers {
    private RunAllServers() {

    }
    public static void main(String[] args) {
        BaseServer[] servers = new BaseServer[9];
        servers[0] = new NewQueryNewConnection();
        servers[1] = new NewQueryNewThread();
        servers[2] = new servers.UDP.NewQueryNewThread();
        servers[3] = new CachedThreadPool();
        servers[4] = new CachedThreadPoolServer();
        servers[5] = new FixedThreadPool();
        servers[6] = new servers.NIO.FixedThreadPool();
        servers[7] = new OneClientOneThread();
        servers[8] = new OneThread();
        for (int i = 0; i < 9; i++) {
            servers[i].start();
        }
        System.out.println("Servers ready!");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String command = scanner.next();
            if (command.equals("exit")) {
                return;
            }
        }
    }
}
