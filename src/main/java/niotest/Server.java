package niotest;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.io.*;
import java.util.*;

public class Server implements Runnable {
    private final InetAddress address;
    private final int port;

    private ServerSocketChannel serverSocketChannel;

    private Selector selector;

    public static void main(String ... args) {
        try {
            Server server = new Server(InetAddress.getLocalHost(), 19090);
            server.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public Server(InetAddress address, int port) throws IOException {
        this.address = address;
        this.port = port;
        this.serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(new InetSocketAddress(address, port));

        selector = SelectorProvider.provider().openSelector();
        
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    public void start() {
        new Thread(this).start();
        System.out.println("Server started on port " + port);
    }
    

    public void run() {
        while (true) {
            try {

                selector.select();
                
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while(it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();
                    if (key.isAcceptable()) {
                        System.out.println("Accepted connection by key: " + key.channel());
                        SocketChannel cc = serverSocketChannel.accept();
                        cc.configureBlocking(false);
                        cc.register(selector, SelectionKey.OP_READ);
                    } if (key.isReadable()) {
                        SocketChannel sc = (SocketChannel)key.channel();
                        System.out.println("Reading from channel: " + key.channel()); 
                        ByteBuffer bb = ByteBuffer.allocate(100);
                        int readed = sc.read(bb);
                        System.out.println("Readed: " + readed);
                        while (readed > 0) {
                            
                            System.out.print(new String(bb.array(), 0, readed));
                            bb.clear();
                            readed = sc.read(bb);
                        }
                        key.cancel();
                        sc.close();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
}
