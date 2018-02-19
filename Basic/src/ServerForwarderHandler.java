import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;


public class ServerForwarderHandler extends ChannelInboundHandlerAdapter {
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Channel Active");
    Basic.serverChannels.add(ctx.channel());
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws IOException, Exception {

    ByteBuf f = (ByteBuf) msg;
    String m = new String();
    while (f.isReadable()) {
      m = m + (char) f.readByte();
    }
    if (!m.contains("exit")) {
      Channel c = Basic.serverChannels.get(0);
      if (c.id() == ctx.channel().id())
        c = Basic.serverChannels.get(1);
      f.resetReaderIndex();
      c.write(f);
      c.flush();
    }
    else {
      ctx.close();
    }
  }
}
