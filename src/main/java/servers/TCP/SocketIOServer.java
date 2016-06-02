package servers.TCP;

import servers.BaseServer;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by n_buga on 29.05.16.
 */
public abstract class SocketIOServer implements BaseServer {
    protected boolean end;

    public SocketIOServer() {
        end = false;
    }

    @Override
    public void start() {
        end = false;
        Thread server = new Thread(this::handlerConnection);
        server.start();
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected abstract void handlerConnection();

    protected void handlerQuery(Utils.Connection connection) {
        while (!end) {
            if (!oneQueryHandler(connection)) {
                break;
            }
        }
        connection.close();
    }

    protected boolean oneQueryHandler(Utils.Connection connection) {
        int arrayByteSize;
        try {
            arrayByteSize = connection.fromConnection.readInt();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        long beginQueryHandler = System.currentTimeMillis();
        if (arrayByteSize == -1) {
            return false;
        }

        byte[] byteArray = new byte[arrayByteSize];
        Protocol.ArrayProto arrayProto;
        try {
            int readBytes = connection.fromConnection.read(byteArray);
            if (readBytes != arrayByteSize) {
                return false;
            }
            arrayProto = Protocol.ArrayProto.parseFrom(byteArray);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        List<Integer> array = arrayProto.getElementList();

        long beginQueryCount = System.currentTimeMillis();
        array = Utils.sort(array);
        long endQueryCount = System.currentTimeMillis();
        Protocol.ArrayProto result = Protocol.ArrayProto.newBuilder().addAllElement(array).build();

        try {
            connection.toConnection.writeInt(result.getSerializedSize());
            byte[] arrayByte = result.toByteArray();
            connection.toConnection.write(arrayByte);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        long endQueryHandler = System.currentTimeMillis();
        try {
            connection.toConnection.writeLong(endQueryHandler - beginQueryHandler);
            connection.toConnection.writeLong(endQueryCount - beginQueryCount);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
