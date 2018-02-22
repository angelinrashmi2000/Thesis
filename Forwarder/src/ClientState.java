import java.nio.ByteBuffer;


public class ClientState {
  protected ByteBuffer     sendBuf, recvBuf;
  //for testing capacity is set to 10
  private static final int CAPACITY = 10;   //1350;
  int                      fortesting;

  public ClientState() {
    sendBuf = ByteBuffer.allocate(CAPACITY);
    recvBuf = ByteBuffer.allocate(CAPACITY);
    fortesting = 33;
  }
}
