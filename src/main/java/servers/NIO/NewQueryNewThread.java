package servers.NIO;

import javafx.util.Pair;
import utils.Protocol;
import utils.Utils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by n_buga on 31.05.16.
 */
public class NewQueryNewThread extends NIOServer {
    final private int PORT = 9993;

    private class RealArraySortedArray {
        private List<Integer> real;
        private List<Integer> sorted;
        private Thread curThread;
        private long beginTimeQueryCount;
        private long endTimeQueryCount;

        public RealArraySortedArray(List<Integer> real) {
            this.real = real;
        }

        public void setThread(Thread thread) {
            curThread = thread;
        }

        public Thread getThread() {
            return curThread;
        }

        public void sort() {
            beginTimeQueryCount = System.currentTimeMillis();
            sorted = Utils.sort(real);
            endTimeQueryCount = System.currentTimeMillis();
        }

        public List<Integer> getSorted() {
            return sorted;
        }

        public long getCountTime() {
            return endTimeQueryCount - beginTimeQueryCount;
        }
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
                final RealArraySortedArray result = new RealArraySortedArray(array);
                result.setThread(new Thread(result::sort));
                result.getThread().start();
                try {
                    result.getThread().join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }

                selectionKey = selectionKey.interestOps(SelectionKey.OP_WRITE);

                selectionKey.attach(new Pair<>(result,
                        beginTimeQueryHandler));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (selectionKey.isWritable()) {
            Pair<RealArraySortedArray, Long> attachObject = (Pair<RealArraySortedArray, Long>) selectionKey.attachment();
            List<Integer> result = null;
            try {
                attachObject.getKey().getThread().join();
                result = attachObject.getKey().getSorted();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

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
            times.putLong(System.currentTimeMillis() - attachObject.getValue());
            times.putLong(attachObject.getKey().getCountTime());
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
