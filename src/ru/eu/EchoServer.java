package ru.eu;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Set;

public class EchoServer {
    private ServerSocketChannel server;
    private Selector selector;
    private ByteBuffer buffer;

    public EchoServer() throws Exception {
        buffer = ByteBuffer.allocate(256);
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(9000));
        selector = Selector.open();
        server.configureBlocking(false);
        server.register(selector, SelectionKey.OP_ACCEPT);
        handleSelect();
    }

    private void handleSelect() throws Exception {
        while (server.isOpen()) {
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()){
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }

    private void handleAccept(SelectionKey key) throws Exception{
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        channel.register(selector, key.OP_READ);
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        StringBuilder sb = new StringBuilder();
        while (true) {
            int read = channel.read(buffer);
            if (read == -1) {
                channel.close();
                return;
            }
            if (read == 0) {
                break;
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                sb.append((char) buffer.get());
            }
            buffer.clear();
        }
        channel.write(ByteBuffer.wrap(sb.toString().getBytes(StandardCharsets.UTF_8)));
    }
}
