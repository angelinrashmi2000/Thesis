
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
      ServerBootstrap b = new ServerBootstrap(); // (2)
      b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class) // (3)
          .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
              ch.pipeline().addLast(new ServerForwarderHandler());
            }
          }).option(ChannelOption.SO_BACKLOG, 128) // (5)
          .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

      // Bind and start to accept incoming connections.
      ChannelFuture f = null;
      for (int port : ports) {
        f = b.bind(new InetSocketAddress("localhost", port)).sync();
      }

      // Wait until the server socket is closed.
      // In this example, this does not happen, but you can do that to gracefully
      // shut down your server.
      // for (Channel f : serverChannels)
      f.channel().closeFuture().sync();

    } finally {
      System.out.println("finally");
      workerGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }

}
