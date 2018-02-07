import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;


public class SampleServer {

  public static void main(String[] args) throws IOException {
    // TODO Auto-generated method stub
    Selector selector = Selector.open();
    ServerSocketChannel server = ServerSocketChannel.open();
    //server.close();
    server.bind(new InetSocketAddress("localhost", 3333));
    server.configureBlocking(false);
    server.register(selector, SelectionKey.OP_ACCEPT);
    ByteBuffer buffer = ByteBuffer.allocate(50);
    System.out.println("server started");
    int count = 0;
    while (true && server.isOpen()) {

      int numOfChannels = selector.select();
      System.out.println("first while" + numOfChannels);
      Set<SelectionKey> selectedKeys = selector.selectedKeys();
      Iterator<SelectionKey> iter = selectedKeys.iterator();

      while (iter.hasNext()) {
        System.out.println("iter");
        SelectionKey key = iter.next();

        if (key.isAcceptable()) {
          register(selector, server);
        }

        if (key.isReadable()) {
          answerWithEcho(buffer, key);
        }
        iter.remove();
      }
      count++;
      System.out.println("count:" + count);
      if (count == 10000000)
        server.close();
    }
    System.out.println("server closed");

  }

  private static void answerWithEcho(ByteBuffer buffer, SelectionKey key) throws IOException {

    SocketChannel client = (SocketChannel) key.channel();
    client.read(buffer);
    if (new String(buffer.array()).trim().equals("END")) {
      client.close();
      System.out.println("Not accepting client messages anymore");
    }

    buffer.flip();
    client.write(buffer);
    buffer.clear();
  }

  private static void register(Selector selector, ServerSocketChannel serverSocket) throws IOException {
    System.out.println("register client");
    SocketChannel client = serverSocket.accept();
    client.configureBlocking(false);
    client.register(selector, SelectionKey.OP_READ);
  }

  public static Process start() throws IOException, InterruptedException {
    String javaHome = System.getProperty("java.home");
    String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
    String classpath = System.getProperty("java.class.path");
    String className = SampleServer.class.getCanonicalName();

    ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classpath, className);
    System.out.println("process start");
    return builder.start();
  }
  //}

  //}

}
