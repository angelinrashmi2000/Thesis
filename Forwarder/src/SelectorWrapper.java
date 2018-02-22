import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.SSLSession;


public class SelectorWrapper {
  private final Selector            selector;
  private final ServerSocketChannel server;

  public SelectorWrapper(String hostname, int port) throws IOException {
    this.selector = Selector.open();
    this.server = ServerSocketChannel.open();
    this.server.bind(new InetSocketAddress("localhost", 3333));
    this.server.configureBlocking(false);
    server.register(selector, SelectionKey.OP_ACCEPT);
    System.out.println("Server running on port " + port);
  }

  public ServerSocketChannel server() {
    return server;
  }

  public void process() throws IOException {
    SSLSession s;
    while (true) {
      int num = selector.select();
      Set<SelectionKey> selectedkeys = selector.selectedKeys();
      Iterator<SelectionKey> iter = selectedkeys.iterator();
      while (iter.hasNext()) {
        SelectionKey key = iter.next();

        if (key.isAcceptable()) {
          SocketChannel client = server.accept();
          acceptClient(client);
        }

        if (key.isReadable()) {
          parseInput(key);

        }

        iter.remove();
      }
    }
  }

  private void parseInput(SelectionKey key) throws IOException {
    SocketChannel c = (SocketChannel) key.channel();
    ClientState cs = (ClientState) key.attachment();
    c.read(cs.recvBuf); // buf in write mode

    cs.recvBuf.flip(); // switches to read mode
    //  printByteBuffer(cs.recvBuf);

    // parse the input here
    while (cs.recvBuf.limit() > cs.recvBuf.position()) {
      char b = (char) (cs.recvBuf.get()); //get one byte of data
      System.out.println(b);
    }

    // compact the buffer so that the next read has enough space
    cs.recvBuf.compact();

    //not needed now
    /* if (key.isWritable() && cs.sendBuf.position() > 0) {
      c.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
      c.write(cs.sendBuf);
      //clear or compact
      c.register(selector, SelectionKey.OP_READ);
    }*/

  }

  private void printByteBuffer(ByteBuffer buf) {
    System.out.println(buf.position() + " " + buf.limit() + " " + buf.capacity());
    String m = new String(buf.array());
    System.out.println("Buffer Content-->" + m + "end");
    System.out.println("Buffer Length-->" + m.length());

  }

  public void acceptClient(SocketChannel client) throws IOException {
    client.configureBlocking(false);
    ClientState cs = new ClientState();
    SelectionKey selKey = client.register(selector, SelectionKey.OP_READ, cs);
    System.out.println("Client accpeted");
  }
}
