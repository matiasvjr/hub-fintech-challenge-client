package hubfintech.challengeclient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Client {
    public static void main(String[] args){
        try (AsynchronousSocketChannel client = AsynchronousSocketChannel.open()) {
            final Future<Void> result = client.connect(new InetSocketAddress("127.0.0.1", 3000));
            result.get();
            final String str1 = "{\"action\": \"withdraw\",\"cardnumber\":\"1234567812345678\",\"amount\": \"11,00\"}";

            while (true) {
                final ByteBuffer buffer = ByteBuffer.allocate(256);

                buffer.put(str1.getBytes());
                buffer.flip();

                final Future<Integer> write = client.write(buffer);
                System.out.println("Writing to server: " + str1);

                write.get();
                buffer.clear();

                Future<Integer> read = client.read(buffer);
                Integer bytesRead = read.get();

                if (bytesRead >= 0) {
                    System.out.println("Received from server: " + new String(buffer.array(), 0, bytesRead));
                    buffer.clear();

                    Thread.sleep(10);
                } else {
                    System.out.println("Disconnected.");
                    break;
                }
            }
        }
        catch (ExecutionException | IOException e) {
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            System.out.println("Disconnected from the server.");
        }
    }
}
