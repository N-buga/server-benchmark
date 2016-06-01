package utils;

import UI.Parameter;
import client.ClientTCP;
import servers.BaseServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by n_buga on 28.05.16.
 */
public class Utils {

    public static class Connection {
        public DataInputStream fromConnection;
        public DataOutputStream toConnection;
        private Socket socket;
        private String ip;
        private int port;

        public Connection(Socket socket) {
            this.socket = socket;
            try {
                fromConnection = new DataInputStream(socket.getInputStream());
                toConnection = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public Connection(Socket socket, String ip, int port) {
            this.ip = ip;
            this.port = port;
            this.socket = socket;
            try {
                fromConnection = new DataInputStream(socket.getInputStream());
                toConnection = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void close() {
            if (fromConnection != null) {
                try {
                    fromConnection.close();
                } catch (IOException ignored) {
                }
            }
            if (toConnection != null) {
                try {
                    toConnection.close();
                } catch (IOException ignored) {
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }
        }

        public Socket getSocket() {
            return socket;
        }

        public String getIp() {
            return ip;
        }

        public int getPort() {
            return port;
        }
    }


    public static int[] sort(int[] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = i; j < array.length; j++) {
                if (array[i] > array[j]) {
                    int tmp = array[i];
                    array[i] = array[j];
                    array[j] = tmp;
                }
            }
        }
        return array;
    }

    public static List<Integer> sort(List<Integer> array) {
        ArrayList<Integer> arrayList = new ArrayList<>(array);
        for (int i = 0; i < arrayList.size(); i++) {
            for (int j = i; j < arrayList.size(); j++) {
                if (arrayList.get(i) > arrayList.get(j)) {
                    int tmp = arrayList.get(i);
                    arrayList.set(i, arrayList.get(j));
                    arrayList.set(j, tmp);
                }
            }
        }
        return arrayList;
    }

    public static List<Integer> createRandomArray(int n) {
        Random random = new Random();
        List<Integer> randomArray = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            randomArray.add(random.nextInt(500));
        }
        return randomArray;
    }

    public static Long getQueries(BaseServer server, int countOfQueries, int arraySize, int deltaTime,
                                  String ip, ClientTCP client) {
        client.createConnection(server.getPort(), ip);
        long beginTime = System.currentTimeMillis();
        for (int i = 0; i < countOfQueries; i++) {
            List<Integer> array = createRandomArray(arraySize);
            client.sortArray(array);
            try {
                Thread.sleep(deltaTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        client.closeConnection();
        long endTime = System.currentTimeMillis();
        return endTime - beginTime;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static int fromByteArray(byte[] bytes) {
        return ByteBuffer.wrap(bytes).getInt();
    }
}
