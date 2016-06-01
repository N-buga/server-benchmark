package client;

import com.google.common.primitives.Longs;
import org.apache.commons.lang3.ArrayUtils;
import org.omg.CORBA.TIMEOUT;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by n_buga on 01.06.16.
 */
public class ClientUDP extends Client {
    private final int TIMEOUT = 5000;
    private int serverPort;
    private String serverIP;

    public ClientUDP() {
    }

    @Override
    public void createConnection(int port, String ip) {
        serverPort = port;
        serverIP = ip;
    }

    @Override
    public void closeConnection() {
    }

    @Override
    public List<Integer> sortArray(List<Integer> array) {
        Protocol.ArrayProto arrayProto = Protocol.ArrayProto.newBuilder().addAllElement(array).build();
        try (DatagramSocket s = new DatagramSocket(0))
        {
            InetAddress ip = InetAddress.getByName(serverIP);

            byte[] arrayProtoSize = Utils.intToByteArray(arrayProto.getSerializedSize());
            byte[] data = ArrayUtils.addAll(arrayProtoSize, arrayProto.toByteArray());

            DatagramPacket dataPacket = new DatagramPacket(data,
                    data.length, ip, serverPort);
            s.send(dataPacket);

            byte[] receivedData = new byte[20000];
            DatagramPacket receivedDataPacket = new DatagramPacket(receivedData, receivedData.length);
            s.setSoTimeout(TIMEOUT);
            try {
                s.receive(receivedDataPacket);
            } catch (SocketTimeoutException e) {
                return null;
            }

            int readBytes = 0;
            byte[] byteSize = ArrayUtils.subarray(receivedData, readBytes, readBytes + 4);
            readBytes += byteSize.length;
            byte[] byteArray = ArrayUtils.subarray(receivedData, readBytes, readBytes + Utils.fromByteArray(byteSize));
            readBytes += byteArray.length;
            byte[] timeQueryHandlerBytes = ArrayUtils.subarray(receivedData, readBytes, readBytes + 8);
            readBytes += 8;
            byte[] timeQueryCountBytes = ArrayUtils.subarray(receivedData, readBytes, readBytes + 8);

            timeQueryCount.add(Longs.fromByteArray(timeQueryCountBytes));
            timeQueryHandler.add(Longs.fromByteArray(timeQueryHandlerBytes));

            Protocol.ArrayProto arraySortedProto = Protocol.ArrayProto.parseFrom(byteArray);

            return arraySortedProto.getElementList();

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
