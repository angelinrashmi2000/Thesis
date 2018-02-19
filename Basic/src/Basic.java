
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;


public class Basic {
  static final List<Integer> ports          = Arrays.asList(3333, 3334);
  static final List<Channel> serverChannels = new ArrayList<Channel>(ports.size());

  public static void main(String[] args) throws Exception {
    new Basic().run();

  }

  public void run() throws Exception {
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
          ch.pipeline().addLast(new ServerForwarderHandler());
        }
      }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);

      // Bind and start to accept incoming connections.
      ChannelFuture f = null;
      for (int port : ports) {
        f = b.bind(new InetSocketAddress("localhost", port)).sync();
      }
      // for (Channel f : serverChannels)
      f.channel().closeFuture().sync();

    } finally {
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

}
