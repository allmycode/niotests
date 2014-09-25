package niotest;

import java.net.*;
import java.io.*;

public class Client {
    public static void main(String ... args) throws Exception {
        System.out.println("Running client...");
        InetAddress localhost = InetAddress.getLocalHost();
        System.out.println("Localhost is " + localhost);
        Socket c = new Socket(localhost, 19090);
        OutputStream cs = c.getOutputStream();
        PrintWriter cpw = new PrintWriter(cs);
        cpw.write("hello from client\n");
        cpw.flush();      
    }
}
