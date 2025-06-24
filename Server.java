import java.io.*;
import java.net.*;

public class Server {
    private static final short TYPE_INIT = 1;
    private static final short TYPE_AGREE = 2;
    private static final short TYPE_REQUEST = 3;
    private static final short TYPE_ANSWER = 4;

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java Server <port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                // 为每个客户端创建新线程处理
                new Thread(() -> handleClient(clientSocket)).start();
            }
        }
    }

    private static void handleClient(Socket socket) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // 接收初始化，发送同意
            if (in.readShort() != TYPE_INIT) {
                System.out.println("错误：不是初始化报文");
                return;
            }
            int blockCount = in.readInt();
            out.writeShort(TYPE_AGREE);
            out.flush();

            // 处理每个数据块
            for (int i = 0; i < blockCount; i++) {
                if (in.readShort() != TYPE_REQUEST) {
                    System.out.println("错误：不是请求报文");
                    return;
                }
                int len = in.readInt();
                byte[] data = new byte[len];
                in.readFully(data);

                // 反转并发送回去
                String reversed = new StringBuilder(new String(data)).reverse().toString();
                out.writeShort(TYPE_ANSWER);
                out.writeInt(reversed.length());
                out.write(reversed.getBytes());
                out.flush();
            }
        } catch (Exception e) {
            System.out.println("处理客户端出错: " + e.getMessage());
        }
        try { socket.close(); } catch (Exception ignored) {}
    }
}