
public class QuicHeader {
  public enum packetType {
    PUBLIC_FLAG_VERSION, PUBLIC_RESET_PACKET, CONN_ID_SIZE, PACKET_NUMBER
  }

  private static final byte PUBLIC_FLAG_VERSION  = 1;
  private static final byte PUBLIC_FLAG_RESET    = 2;
  private static final byte PUBLIC_CONN_ID_SIZE  = 12;
  private static final byte PUBLIC_PACKET_NUMBER = 48;

  /* 0x01 = PUBLIC_FLAG_VERSION.  Interpretation of this flag
      depends on whether the packet is sent by the server or the
      client.  When sent by the client, setting it indicates that the
      header contains a QUIC Version (see below).  This bit must be
      set by a client in all packets until confirmation from the
      server arrives agreeing to the proposed version is received by
      the client.  A server indicates agreement on a version by
      sending packets without setting this bit.  When this bit is set
      by the server, the packet is a Version Negotiation Packet.
      Version Negotiation is described in more detail later.
  
   *  0x02 = PUBLIC_FLAG_RESET.  Set to indicate that the packet is a
      Public Reset packet.
  
   *  Two bits at 0x0C indicate the size of the Connection ID that is
      present in the packet.  These bits must be set to 0x0C in all
      packets until negotiated to a different value for a given
      direction (e.g., client may request fewer bytes of the
      Connection ID be presented).
  
      +  0x0C indicates an 8-byte Connection ID is present
  
      +  0x08 indicates that a 4-byte Connection ID is present
  
      +  0x04 indicates that a 1-byte Connection ID is used
  
      +  0x00 indicates that the Connection ID is omitted
  
   *  Two bits at 0x30 indicate the number of low-order-bytes of the
      packet number that are present in each packet.  The bits are
      only used for Frame Packets.  For Public Reset and Version
      Negotiation Packets (sent by the server) which don't have a
      packet number, these bits are not used and must be set to 0.
      Within this 2 bit mask:
  
      +  0x30 indicates that 6 bytes of the packet number is present
  
      +  0x20 indicates that 4 bytes of the packet number is present
  
      +  0x10 indicates that 2 bytes of the packet number is present
  
      +  0x00 indicates that 1 byte of the packet number is present
  
   *  0x40 is reserved for multipath use.
  
   *  0x80 is currently unused, and must be set to 0. */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    byte msg = 0;
    packetType pt;
    int conn_length;
    int packet_nr;
    // reading quic header
    switch (msg & PUBLIC_FLAG_VERSION) {
      case PUBLIC_FLAG_VERSION:

      case PUBLIC_FLAG_RESET:
        if ((msg & PUBLIC_FLAG_RESET) == PUBLIC_FLAG_RESET)
          pt = packetType.PUBLIC_RESET_PACKET;
      case PUBLIC_CONN_ID_SIZE:
        if ((msg & PUBLIC_FLAG_VERSION) == 12)
          conn_length = 8;
        else if ((msg & PUBLIC_FLAG_VERSION) == 8)
          conn_length = 4;
        else if ((msg & PUBLIC_FLAG_VERSION) == 4)
          conn_length = 1;
        else if ((msg & PUBLIC_FLAG_VERSION) == 0)
          conn_length = 0;
      case PUBLIC_PACKET_NUMBER:
        if ((msg & PUBLIC_PACKET_NUMBER) == 48)
          packet_nr = 6;
        else if ((msg & PUBLIC_PACKET_NUMBER) == 32)
          packet_nr = 4;
        else if ((msg & PUBLIC_PACKET_NUMBER) == 16)
          packet_nr = 2;
        else if ((msg & PUBLIC_PACKET_NUMBER) == 0)
          packet_nr = 1;
        if (conn_length != 0) {

        }
    }

  }

}
