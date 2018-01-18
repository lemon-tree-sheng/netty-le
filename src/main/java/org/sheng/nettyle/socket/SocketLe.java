package org.sheng.nettyle.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 传统 socket 编程，单线程处理
 *
 * @author shengxingyue, created on 2018/1/18
 */
@Slf4j
public class SocketLe {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(10101);
        log.info("服务端启动");

        while (true) {
            Socket socket = serverSocket.accept();
            log.info("client connected...");

            // 处理客户端链接
            handleClient(socket);
        }

    }

    private static void handleClient(Socket socket) throws IOException {
        byte[] cache = new byte[1024];

        try (InputStream is = socket.getInputStream()) {
            while (true) {
                int bytes = is.read(cache);
                if (bytes != -1) {
                    System.out.println(new String(cache, 0, bytes));
                } else {
                    break;
                }
            }
        } finally {
            socket.close();
        }

    }
}
