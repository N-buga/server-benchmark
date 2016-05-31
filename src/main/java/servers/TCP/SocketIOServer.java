package servers.TCP;

import servers.BaseServer;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
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
        int arrayByteSize;
        while (!end) {
            long beginQueryHandler = System.currentTimeMillis();
            try {
                arrayByteSize = connection.fromConnection.readInt();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            if (arrayByteSize == -1) {
                break;
            }

            byte[] byteArray = new byte[arrayByteSize];
            Protocol.ArrayProto arrayProto;
            try {
                int readBytes = connection.fromConnection.read(byteArray);
                if (readBytes != arrayByteSize) {
                    break;
                }
                arrayProto = Protocol.ArrayProto.parseFrom(byteArray);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }

            List<Integer> array = arrayProto.getElementList();

            long beginQueryCount = System.currentTimeMillis();
            array = Utils.sort(array);
            long endQueryCount = System.currentTimeMillis();
            Protocol.ArrayProto result = Protocol.ArrayProto.newBuilder().addAllElement(array).build();

            try {
                connection.toConnection.writeInt(result.getSerializedSize());
                result.writeTo(connection.toConnection);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            long endQueryHandler = System.currentTimeMillis();
            try {
                connection.toConnection.writeLong(endQueryHandler - beginQueryHandler);
                connection.toConnection.writeLong(endQueryCount - beginQueryCount);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
        connection.close();
    }

}
