package servers.UDP;

import servers.BaseServer;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by n_buga on 01.06.16.
 */
abstract public class UDPServer implements BaseServer {
    protected boolean end;

    public UDPServer() {
        end = false;
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

    abstract public int getPort();

    private void handlerConnection() {
        try (DatagramSocket s = new DatagramSocket(getPort())) {
            s.setSoTimeout(TIMEOUT);
            while (!end) {
                int sizeByteArray;
                byte[] byteSize = new byte[4];
                DatagramPacket sizePacket = new DatagramPacket(byteSize, byteSize.length);
                try {
                    s.receive(sizePacket);
                } catch (SocketTimeoutException e) {
                    continue;
                }
                sizeByteArray = Utils.fromByteArray(byteSize);
                if (sizeByteArray == -1) {
                    continue;
                }
                byte[] byteArray = new byte[sizeByteArray];
                DatagramPacket arrayPacket = new DatagramPacket(byteArray, byteArray.length);
                s.receive(arrayPacket);
                Protocol.ArrayProto arrayForSorted = Protocol.ArrayProto.parseFrom(byteArray);
                handlerQuery(arrayPacket.getPort(), arrayPacket.getAddress().getHostName(),
                    arrayForSorted.getElementList());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    protected void countAndSend(int remotePort, String remoteAddress, List<Integer> arrayForSorted) {
        List<Integer> result = Utils.sort(arrayForSorted);

        Protocol.ArrayProto arrayProto = Protocol.ArrayProto.newBuilder().addAllElement(result).build();

        try (DatagramSocket s = new DatagramSocket())
        {
            InetAddress ip = InetAddress.getByName(remoteAddress);
            byte[] arrayProtoSize = Utils.intToByteArray(arrayProto.getSerializedSize());
            DatagramPacket sizePacket = new DatagramPacket(arrayProtoSize,
                    arrayProtoSize.length, ip, remotePort);
            s.send(sizePacket);

            DatagramPacket arrayPacket = new DatagramPacket(arrayProto.toByteArray(), arrayProto.getSerializedSize(),
                    ip, remotePort);
            s.send(arrayPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract protected void handlerQuery(int remotePort, String remoteAddress, List<Integer> arrayForSorted);
}
