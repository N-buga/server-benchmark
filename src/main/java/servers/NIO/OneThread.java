package servers.NIO;

import javafx.util.Pair;
import servers.BaseServer;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by n_buga on 28.05.16.
 */
public class OneThread extends NIOServer {
    private final int PORT = 9997;

    public OneThread() {
        super();
    }

    @Override
    protected void handlerSelectionKey(Selector selector, SelectionKey selectionKey) {
        if (selectionKey.isAcceptable()) {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
            SocketChannel socketChannel;
            try {
                socketChannel = serverSocketChannel.accept();
                socketChannel.configureBlocking(false);
            } catch (IOException e) {
                System.out.printf("The problem with accept a connection\n");
                return;
            }
            try {
                socketChannel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                System.out.printf("The problem with register a new client\n");
            }
        } else if (selectionKey.isReadable()) {
            long beginTimeQueryHandler = System.currentTimeMillis();
            ByteBuffer byteSizeMsg = ByteBuffer.allocate(4);
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            try {
                int readBytes = 0;
                while (readBytes != 4) {
                    readBytes += socketChannel.read(byteSizeMsg);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.printf("Can't read from channel\n");
            }
            byteSizeMsg.flip();
            int sizeMsg = byteSizeMsg.getInt();
            if (sizeMsg == -1) {
                selectionKey.cancel();
                try {
                    socketChannel.close();
                } catch (IOException ignored) {
                }
                return;
            }
            ByteBuffer message = ByteBuffer.allocate(sizeMsg);
            try {
                int readBytes = 0;
                while (readBytes < sizeMsg) {
                    readBytes += socketChannel.read(message);
                }

                Protocol.ArrayProto arrayProto = Protocol.ArrayProto.parseFrom(message.array());
                List<Integer> array = arrayProto.getElementList();
                long beginTimeQueryCount = System.currentTimeMillis();
                List<Integer> result = Utils.sort(array);
                long endTimeQueryCount = System.currentTimeMillis();
                selectionKey = selectionKey.interestOps(SelectionKey.OP_WRITE);
                selectionKey.attach(new Pair<>(result,
                        new long[]{beginTimeQueryHandler, endTimeQueryCount - beginTimeQueryCount}));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectionKey.isWritable()) {
            Pair<List<Integer>, long[]> attachObject = (Pair<List<Integer>, long[]>) selectionKey.attachment();
            List<Integer> result = attachObject.getKey();

            Protocol.ArrayProto array = Protocol.ArrayProto.newBuilder().addAllElement(result).build();

            ByteBuffer byteSizeMsg = ByteBuffer.allocate(4);
            byteSizeMsg.putInt(array.getSerializedSize());
            byteSizeMsg.flip();
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            if (!writeAllBuffer(socketChannel, byteSizeMsg)) {
                return;
            }

            ByteBuffer resultBuffer = ByteBuffer.allocate(array.getSerializedSize());
            resultBuffer.put(array.toByteArray());
            resultBuffer.flip();
            writeAllBuffer(socketChannel, resultBuffer);

            ByteBuffer times = ByteBuffer.allocate(16);
            long timeQueryHandler = System.currentTimeMillis() - attachObject.getValue()[0];
            times.putLong(timeQueryHandler);
            times.putLong(attachObject.getValue()[1]);
            times.flip();
            writeAllBuffer(socketChannel, times);
            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

    @Override
    public int getPort() {
        return PORT;
    }

}
