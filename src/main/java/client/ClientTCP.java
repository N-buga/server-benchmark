package client;

import com.google.protobuf.InvalidProtocolBufferException;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by n_buga on 28.05.16.
 */
public class ClientTCP {
    private Utils.Connection connection;
    private List<Long> timeQueryHandler = new ArrayList<>();
    private List<Long> timeQueryCount = new ArrayList<>();

    public ClientTCP() {
    }

    public void createConnection(int port, String ip) {
        try {
            Socket clientSocket = new Socket(ip, port);
            connection = new Utils.Connection(clientSocket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connection.toConnection.writeInt(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.close();
    }

    public List<Integer> sortArray(List<Integer> array) {
        try {
            Protocol.ArrayProto arrayProto = Protocol.ArrayProto.newBuilder().addAllElement(array).build();
            connection.toConnection.writeInt(arrayProto.getSerializedSize());
            connection.toConnection.write(arrayProto.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        int countBytes = 0;
        try {
            countBytes = connection.fromConnection.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        byte[] byteArray = new byte[countBytes];
        try {
            int readBytes = 0;
            for (int i = 0; i < 20; i++) {
                readBytes += connection.fromConnection.read(byteArray);
                if (readBytes == countBytes) {
                    break;
                }
            }
            if (readBytes != countBytes) {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Protocol.ArrayProto sortedArray;
        try {
            sortedArray = Protocol.ArrayProto.parseFrom(byteArray);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }

        if (sortedArray.getElementList().size() != array.size()) {
            throw new IllegalStateException("Returned array size isn't correct.");
        }
        try {
            timeQueryHandler.add(connection.fromConnection.readLong());
            timeQueryCount.add(connection.fromConnection.readLong());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sortedArray.getElementList();
    }

    public double getHandlerTimeQuery() {
        return ((double) timeQueryHandler.stream().reduce(0L, (a, b) -> a + b))/ timeQueryHandler.size();
    }

    public double getHandlerCountQuery() {
        return ((double) timeQueryCount.stream().reduce(0L, (a, b) -> a + b))/ timeQueryCount.size();
    }
}
