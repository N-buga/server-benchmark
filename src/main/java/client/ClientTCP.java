package client;

import com.google.protobuf.InvalidProtocolBufferException;
import org.omg.CORBA.TIMEOUT;
import utils.Protocol;
import utils.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by n_buga on 28.05.16.
 */
public class ClientTCP extends Client{
    private Utils.Connection connection;

    public ClientTCP() {
    }

    @Override
    public void createConnection(int port, String ip) {
        try {
            Socket clientSocket = new Socket(ip, port);
            connection = new Utils.Connection(clientSocket, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeConnection() {
        try {
            connection.toConnection.writeInt(-1);
        } catch (IOException ignored) {
        }
        connection.close();
    }

    @Override
    public List<Integer> sortArray(List<Integer> array) {
        Protocol.ArrayProto arrayProto = Protocol.ArrayProto.newBuilder().addAllElement(array).build();
        int countBytes;
        byte[] byteArray;;
        try {
            connection.toConnection.writeInt(arrayProto.getSerializedSize());
            connection.toConnection.write(arrayProto.toByteArray());
            countBytes = connection.fromConnection.readInt();
            byteArray = new byte[countBytes];
            int readBytes = 0;
            while (readBytes != countBytes) {
                readBytes += connection.fromConnection.read(byteArray, readBytes, countBytes - readBytes);
            }
        } catch (SocketException | EOFException e) {
            connection.close();
            createConnection(connection.getPort(), connection.getIp());
            try {
                connection.toConnection.writeInt(arrayProto.getSerializedSize());
                connection.toConnection.write(arrayProto.toByteArray());
                countBytes = connection.fromConnection.readInt();
                byteArray = new byte[countBytes];
                int readBytes = 0;
                while (readBytes != countBytes) {
                    readBytes += connection.fromConnection.read(byteArray, readBytes, countBytes - readBytes);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        }
        catch (IOException e) {
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
}
