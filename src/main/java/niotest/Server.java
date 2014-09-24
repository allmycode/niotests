package niotest;

import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.io.*;

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
                
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }
    }
}
