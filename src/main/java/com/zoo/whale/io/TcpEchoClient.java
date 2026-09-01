package com.zoo.whale.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @Date: 2023年 12月 07日
 * @Time:21时:15分
 * @Author: weixin.liu
 * @Version:V1.0
 */
public class TcpEchoClient {
    private Socket socket = null;

    public TcpEchoClient(String serverIp,int port) throws IOException {
        // 这个操作相当于让客户端和服务器建立 tcp 连接.
        // 这里的连接连上了, 服务器的 accept 就会返回.
        this.socket = new Socket(serverIp,port);
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream();
             Scanner scannerFromSocket = new Scanner(inputStream);
             PrintWriter printWriter = new PrintWriter(outputStream)){

            while (true) {
                // 1. 从键盘上读取用户输入的内容.
                System.out.print("-> ");
                String request = scanner.next();
                // 2. 把读取的内容构造成请求, 发送给服务器.
                //    注意, 这里的发送, 是带有换行的!!
                printWriter.println(request);
                printWriter.flush();// 手动刷新缓冲区
                // 3. 从服务器读取响应内容
                String response = scannerFromSocket.next();
                // 4. 把响应结果显示到控制台上.
                System.out.printf("req: %s; resp: %s\n", request, response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        // 实例化客户端对象
        //TcpEchoClient tcpEchoClient = new TcpEchoClient("127.0.0.1", 6666);
        TcpEchoClient tcpEchoClient = new TcpEchoClient("127.0.0.1", 7000);
        // 启动客户端主逻辑
        tcpEchoClient.start();
    }
}
