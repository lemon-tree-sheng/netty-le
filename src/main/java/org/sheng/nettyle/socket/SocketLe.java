package org.sheng.nettyle.socket;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 传统 socket 编程，多线程处理
 *
 * @author shengxingyue, created on 2018/1/18
 */
@Slf4j
public class SocketLe {

    public static void main(String[] args) throws IOException {
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

        ServerSocket serverSocket = new ServerSocket(10101);
        log.info("服务端启动");

        while (true) {
            Socket socket = serverSocket.accept();
            log.info("client connected...");

            // 处理客户端链接
            threadPoolExecutor.execute(() -> {
                try {
                    handleClient(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

    }

    private static void handleClient(Socket socket) throws IOException {
        String currentThreadName = Thread.currentThread().getName();
        log.info(currentThreadName + " 处理中...");
        byte[] cache = new byte[1024];

        try (InputStream is = socket.getInputStream()) {
            while (true) {
                int bytes = is.read(cache);
                if (bytes != -1) {
                    System.out.println(new String(cache, 0, bytes));
                    // 已经读完则结束链接
                    if (bytes < 1023) {
                        log.info(currentThreadName + "  处理结束。。。");
                        break;
                    }
                } else {
                    break;
                }
            }
        } finally {
            socket.close();
        }

    }
}
