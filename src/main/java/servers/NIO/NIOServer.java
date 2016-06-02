package servers.NIO;

import servers.BaseServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by n_buga on 31.05.16.
 */
abstract public class NIOServer implements BaseServer {
    protected Selector selector;
    protected boolean end;

    public NIOServer() {
        try {
            selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        end = false;
        (new Thread(this::startWork)).start();
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        end = true;
        Set<SelectionKey> selectionKeys;
        try {
            selectionKeys = selector.selectedKeys();
        } catch (ClosedSelectorException ignored) {
            return;
        }
        for (SelectionKey selectionKey: selectionKeys) {
            selectionKey.cancel();
            try {
                selectionKey.channel().close();
            } catch (IOException ignored) {
            }
        }
        try {
            selector.close();
        } catch (IOException ignored) {
        }
        try {
            Thread.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected void clientsHandler(Selector selector) {
        while (!end) {
            try {
                int countReady = selector.select(TIMEOUT);
                if (countReady == 0) {
                    continue;
                }
            } catch (IOException e) {
                System.out.printf("The problems with select from Selector.\n");
                return;
            }
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectionKeySet.iterator();
            while (keyIterator.hasNext()) {
                SelectionKey curSelectionKey = keyIterator.next();
                handlerSelectionKey(selector, curSelectionKey);
                keyIterator.remove();
            }
        }
    }

    private void startWork() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
             Selector selector = Selector.open()) {
            serverSocketChannel.bind(new InetSocketAddress(getPort()), 100);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            clientsHandler(selector);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("Can't open ServerSocketChannel or Selector\n");
        }
    }

    abstract protected void handlerSelectionKey(Selector selector, SelectionKey selectionKey);

    protected boolean writeAllBuffer(SocketChannel socketChannel, ByteBuffer fromBuffer) {
        try {
            while (fromBuffer.hasRemaining()) {
                socketChannel.write(fromBuffer);
            }
        } catch (IOException e) {
//            e.printStackTrace();
            return false;
        }
        return true;
    }

}
