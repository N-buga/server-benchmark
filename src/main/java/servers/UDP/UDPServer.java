package servers.UDP;

import org.apache.commons.lang3.ArrayUtils;
import servers.BaseServer;
import utils.Protocol;
import utils.Utils;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.net.*;
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
//            s.setSoTimeout(TIMEOUT);
            while (!end) {
                int sizeByteArray;
                byte[] data = new byte[200000];
                DatagramPacket dataPacket = new DatagramPacket(data, data.length);
                try {
                    s.receive(dataPacket);
                } catch (SocketTimeoutException e) {
                    continue;
                }

                long beginQueryHandler = System.currentTimeMillis();
                byte[] size = ArrayUtils.subarray(data, 0, 4);
                sizeByteArray = Utils.fromByteArray(size);
                if (sizeByteArray == -1) {
                    System.out.println(-1);
                    continue;
                }
                byte[] byteArray = ArrayUtils.subarray(data, 4, sizeByteArray + 4);

                Protocol.ArrayProto arrayForSorted = Protocol.ArrayProto.parseFrom(byteArray);
                handlerQuery(dataPacket.getPort(), dataPacket.getAddress(),
                    arrayForSorted.getElementList(), beginQueryHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    protected void countAndSend(int remotePort, InetAddress remoteAddress, List<Integer> arrayForSorted,
                                long beginQueryHandler) {
        long beginQueryCount = System.currentTimeMillis();
        List<Integer> result = Utils.sort(arrayForSorted);
        long endQueryCount = System.currentTimeMillis();

        Protocol.ArrayProto arrayProto = Protocol.ArrayProto.newBuilder().addAllElement(result).build();

        try (DatagramSocket s = new DatagramSocket())
        {
            byte[] arrayProtoSize = Utils.intToByteArray(arrayProto.getSerializedSize());
            byte[] data = ArrayUtils.addAll(arrayProtoSize, arrayProto.toByteArray());
            data = ArrayUtils.addAll(data, Utils.longToByteArray(System.currentTimeMillis() - beginQueryHandler));
            data = ArrayUtils.addAll(data, Utils.longToByteArray(endQueryCount - beginQueryCount));

            DatagramPacket dataPacket = new DatagramPacket(data,
                    data.length, remoteAddress, remotePort);
            s.send(dataPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract protected void handlerQuery(int remotePort, InetAddress remoteAddress, List<Integer> arrayForSorted,
                                         long beginQueryHandler);
}
