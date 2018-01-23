import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


public class SampleClient {
  SocketChannel c1;
  ByteBuffer    msg;

  public SampleClient() throws IOException {
    c1 = SocketChannel.open(new InetSocketAddress("localhost", 3333));
    msg = ByteBuffer.allocate(50);
  }

  public String sendMsg(String send) throws IOException {
    msg = ByteBuffer.wrap(send.getBytes());
    c1.write(msg);
    msg.clear();
    String resp;
    return resp;
  }
}
