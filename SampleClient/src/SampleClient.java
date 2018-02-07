import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class SampleClient {

  public static void main(String[] args) throws IOException {
    // TODO Auto-generated method stub
    SocketChannel c1, c2;
    ByteBuffer msg;
    c1 = SocketChannel.open(new InetSocketAddress("localhost", 3333));
    String send = "Hellooo";
    msg = ByteBuffer.allocate(50);
    msg = ByteBuffer.wrap(send.getBytes());
    c2 = SocketChannel.open(new InetSocketAddress("localhost", 3333));
    c1.write(msg);
    System.out.println("sent msg:" + send);
    msg.clear();
    c1.read(msg);
    String resp = new String(msg.array()).trim();
    System.out.println("received msg:" + resp);
    msg.clear();
  }

}
