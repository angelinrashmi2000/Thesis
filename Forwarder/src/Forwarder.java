import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Forwarder {
  static ServerSocket     s1, s2;
  static ForwarderHandler f1, f2;

  public static void main(String[] args) throws IOException, Exception, SecurityException {

    int choice = 1;
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
        break;
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
