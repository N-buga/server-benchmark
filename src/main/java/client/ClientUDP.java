package client;

import com.google.protobuf.InvalidProtocolBufferException;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by n_buga on 01.06.16.
 */
public class ClientUDP {
    private int serverPort;
    private String serverIP;

    public ClientUDP() {
    }

    public void createConnection(int port, String ip) {
        serverPort = port;
        serverIP = ip;
    }

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

    public List<Integer> sortArray(List<Integer> array) {
        int port;
        Protocol.ArrayProto arrayProto = Protocol.ArrayProto.newBuilder().addAllElement(array).build();
        try (DatagramSocket s = new DatagramSocket())
        {
            port = s.getLocalPort();
            InetAddress ip = InetAddress.getByName(serverIP);
            byte[] arrayProtoSize = Utils.intToByteArray(arrayProto.getSerializedSize());
            DatagramPacket sizePacket = new DatagramPacket(arrayProtoSize,
                    arrayProtoSize.length, ip, serverPort);
            s.send(sizePacket);

            DatagramPacket arrayPacket = new DatagramPacket(arrayProto.toByteArray(), arrayProto.getSerializedSize(),
                    ip, serverPort);
            s.send(arrayPacket);

        } catch (UnknownHostException | SocketException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        int sizeByteArray;

        try (DatagramSocket s = new DatagramSocket(port)){
            byte[] byteSize = new byte [4];
            DatagramPacket sizePacket = new DatagramPacket(byteSize, byteSize.length);
            s.receive(sizePacket);
            sizeByteArray = Utils.fromByteArray(byteSize);

            byte[] byteArray = new byte[sizeByteArray];
            DatagramPacket arrayPacket = new DatagramPacket(byteArray, byteArray.length);
            s.receive(arrayPacket);
            Protocol.ArrayProto arraySortedProto = Protocol.ArrayProto.parseFrom(byteArray);
            return arraySortedProto.getElementList();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
