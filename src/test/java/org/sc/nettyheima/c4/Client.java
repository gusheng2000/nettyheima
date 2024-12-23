package org.sc.nettyheima.c4;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author shi kang
 * @email shikang1008@outlook.com
 * @date 2024/12/23
 * @description Brief description of the class
 */
public class Client {

    public static void main(String[] args) throws IOException {
        SocketChannel open = SocketChannel.open();
        //连接本地8080
        open.connect(new InetSocketAddress("localhost", 8080));
        System.out.println("client start");
    }
}
