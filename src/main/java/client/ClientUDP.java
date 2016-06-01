package client;

import com.google.common.primitives.Longs;
import org.apache.commons.lang3.ArrayUtils;
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
        try (DatagramSocket s = new DatagramSocket())
        {
            InetAddress ip = InetAddress.getByName(serverIP);
            byte[] codeStop = Utils.intToByteArray(-1);
            DatagramPacket sizePacket = new DatagramPacket(codeStop,
                    codeStop.length, ip, serverPort);
            s.send(sizePacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Integer> sortArray(List<Integer> array) {
        int port;
        Protocol.ArrayProto arrayProto = Protocol.ArrayProto.newBuilder().addAllElement(array).build();
        try (DatagramSocket s = new DatagramSocket())
        {
            port = s.getLocalPort();
            InetAddress ip = InetAddress.getByName(serverIP);

            byte[] arrayProtoSize = Utils.intToByteArray(arrayProto.getSerializedSize());
            byte[] data = ArrayUtils.addAll(arrayProtoSize, arrayProto.toByteArray());

            DatagramPacket dataPacket = new DatagramPacket(data,
                    data.length, ip, serverPort);
            s.send(dataPacket);
        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        try (DatagramSocket s = new DatagramSocket(port)){
            byte[] data = new byte[200000];
            DatagramPacket dataPacket = new DatagramPacket(data, data.length);
            s.receive(dataPacket);

            int readBytes = 0;
            byte[] byteSize = ArrayUtils.subarray(data, readBytes, readBytes + 4);
            readBytes += byteSize.length;
            byte[] byteArray = ArrayUtils.subarray(data, readBytes, readBytes + Utils.fromByteArray(byteSize));
            readBytes += byteArray.length;
            byte[] timeQueryHandlerBytes = ArrayUtils.subarray(data, readBytes, readBytes + 8);
            readBytes += 8;
            byte[] timeQueryCountBytes = ArrayUtils.subarray(data, readBytes, readBytes + 8);

            timeQueryCount.add(Longs.fromByteArray(timeQueryCountBytes));
            timeQueryHandler.add(Longs.fromByteArray(timeQueryHandlerBytes));

            Protocol.ArrayProto arraySortedProto = Protocol.ArrayProto.parseFrom(byteArray);

            return arraySortedProto.getElementList();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
