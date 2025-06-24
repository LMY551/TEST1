import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Client {
    private static final short TYPE_INIT = 1;
    private static final short TYPE_AGREE = 2;
    private static final short TYPE_REQUEST = 3;
    private static final short TYPE_ANSWER = 4;

    public static void main(String[] args) throws IOException {
        if (args.length != 6) {
            System.err.println("Usage: java Client <serverIP> <serverPort> <Lmin> <Lmax> <inputFile> <outputFile>");
            System.exit(1);
        }

        String serverIP = args[0];
        int serverPort = Integer.parseInt(args[1]);
        int Lmin = Integer.parseInt(args[2]);
        int Lmax = Integer.parseInt(args[3]);
        String inputFile = args[4];
        String outputFile = args[5];

        // 读取文件并分块
        byte[] fileData = Files.readAllBytes(Paths.get(inputFile));
        List<Integer> blockSizes = new ArrayList<>();
        Random rand = new Random();
        int remaining = fileData.length;

        while (remaining > 0) {
            int blockSize = Math.min(rand.nextInt(Lmax - Lmin + 1) + Lmin, remaining);
            blockSizes.add(blockSize);
            remaining -= blockSize;
        }

        try (Socket socket = new Socket(serverIP, serverPort);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // 发送初始化报文
            out.writeShort(TYPE_INIT);
            out.writeInt(blockSizes.size());
            out.flush();

            // 接收同意报文
            if (in.readShort() != TYPE_AGREE) {
                throw new IOException("Server not ready");
            }

            // 存储反转结果
            byte[][] reversedBlocks = new byte[blockSizes.size()][];
            int offset = 0;

            // 逐块处理
            for (int i = 0; i < blockSizes.size(); i++) {
                int len = blockSizes.get(i);
                byte[] block = Arrays.copyOfRange(fileData, offset, offset + len);
                offset += len;

                // 发送反转请求
                out.writeShort(TYPE_REQUEST);
                out.writeInt(len);
                out.write(block);
                out.flush();

                // 接收反转结果
                if (in.readShort() != TYPE_ANSWER)
                    throw new IOException("Unexpected answer packet");

                int revLen = in.readInt();
                byte[] reversed = new byte[revLen];
                in.readFully(reversed);
                reversedBlocks[i] = reversed;

                System.out.printf("Block %d: %s\n", i+1, new String(reversed));
            }

            // 生成最终反转文件
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                for (int i = blockSizes.size()-1; i >= 0; i--) {
                    fos.write(reversedBlocks[i]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}