package servers.NIO;

import javafx.util.Pair;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by n_buga on 01.06.16.
 */
abstract public class NIOThreadPool extends NIOServer {

    public NIOThreadPool() {
        super();
    }

    abstract protected ExecutorService getExecutorService();

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
            ByteBuffer byteSizeMsg = ByteBuffer.allocate(4);
            SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
            long beginTimeQueryHandler;
            try {
                int readBytes = 0;
                while (readBytes != 4) {
                    readBytes += socketChannel.read(byteSizeMsg);
                }
                beginTimeQueryHandler = System.currentTimeMillis();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.printf("Can't read from channel\n");
                return;
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
                Future<Pair<List<Integer>, Long>> result = getExecutorService().submit(() -> {
                    long beginTimeQueryCount = System.currentTimeMillis();
                    List<Integer> sortedArray = Utils.sort(array);
                    long endTimeQueryCount = System.currentTimeMillis();
                    return new Pair<>(sortedArray, endTimeQueryCount - beginTimeQueryCount);
                });
                selectionKey = selectionKey.interestOps(SelectionKey.OP_WRITE);
                selectionKey.attach(new Pair<>(result,
                        beginTimeQueryHandler));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectionKey.isWritable()) {
            Pair<Future<Pair<List<Integer>, Long>>, Long> attachObject =
                    (Pair<Future<Pair<List<Integer>, Long>>, Long>) selectionKey.attachment();
            List<Integer> sortedArray;
            Pair<List<Integer>, Long> result;
            try {
                result = attachObject.getKey().get();
                sortedArray = result.getKey();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return;
            }

            Protocol.ArrayProto array = Protocol.ArrayProto.newBuilder().addAllElement(sortedArray).build();

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
            times.putLong(System.currentTimeMillis() - attachObject.getValue());
            times.putLong(result.getValue());
            times.flip();
            writeAllBuffer(socketChannel, times);

            selectionKey.interestOps(SelectionKey.OP_READ);
        }
    }

}
