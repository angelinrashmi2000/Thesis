import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public class Forwarder {
  static ServerSocket                    s1, s2;
  static ForwarderHandler                f1, f2;
  static ServerSocketChannel             server;
  static HashMap<Integer, SocketChannel> g = new HashMap<Integer, SocketChannel>();

  public static void main(String[] args) throws IOException, Exception, SecurityException {

    int choice = 2;
    switch (choice) {
      case 1:
        //server listens to telnet client1 on port 3333
        try {
          s1 = new ServerSocket(3333);
          Socket client1 = s1.accept();
          System.out.println("Client1 connected");
          f1 = new ForwarderHandler(client1);
          new Thread(f1).start();

          //server listens to telnet client2 on port 3334
          s2 = new ServerSocket(3334);
          Socket client2 = s2.accept();
          System.out.println("Client2 connected");
          f2 = new ForwarderHandler(client2);
          new Thread(f2).start();
        } catch (IOException e) {
          e.printStackTrace();
        }
        break;
      case 2:
        System.out.println("selectors");
        server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress("localhost", 3333));
        server.configureBlocking(false);
        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);
        int index = 0;
        while (true) {
          int numberOfClients = selector.select();
          Set<SelectionKey> selectedkeys = selector.selectedKeys();
          Iterator<SelectionKey> iter = selectedkeys.iterator();
          while (iter.hasNext()) {
            SelectionKey key = iter.next();
            if (key.isAcceptable()) {
              SocketChannel client = server.accept();
              client.configureBlocking(false);
              client.register(selector, SelectionKey.OP_READ, new Integer(index));
              System.out.println("Client" + (index + 1) + " connected");
              g.put(index, client);
              index = index + 1;
            }
            if (key.isReadable()) {
              SocketChannel c = (SocketChannel) key.channel();
              ByteBuffer dst = ByteBuffer.allocate(50);
              c.read(dst);
              String m = new String(dst.array());
              if (m.contains("exit")) {
                server.close();
                return;
              }
              int index1 = ((Integer) key.attachment()).intValue();
              SocketChannel writeToClient;

              if (index1 == 0)
                writeToClient = g.get(1);
              else
                writeToClient = g.get(0);
              dst.flip();
              writeToClient.write(dst);
              dst.clear();

            }
            iter.remove();
          }
        }
      case 3:
        System.out.println("netty");
        break;
    }
    return;
  }
}

class ForwarderHandler implements Runnable {
  Socket           myClient;
  DataInputStream  in;
  DataOutputStream out;
  String           has_data;

  public ForwarderHandler(Socket client) throws IOException {
    myClient = client;
    in = new DataInputStream(client.getInputStream());
    out = new DataOutputStream(client.getOutputStream());
    has_data = null;
  }

  @Override
  public void run() {
    while (true) {
      try {
        has_data = in.readLine();
        if (has_data != null) {

          // closes the IO data stream, socket connection if the telnet client sends exit
          if (has_data.contains("exit")) {
            in.close();
            out.close();
            if (this.equals(Forwarder.f1))
              Forwarder.s1.close();
            else
              Forwarder.s2.close();
            myClient.close();
            System.out.println("connection closed");
            return;
          }

          //writes data to the other client
          if (this.equals(Forwarder.f1))
            Forwarder.f2.out.writeBytes(has_data + "\n");
          else
            Forwarder.f1.out.writeBytes(has_data + "\n");
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
