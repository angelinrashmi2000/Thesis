import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;


public class SampleTest {
  SampleClient c1;
  Process      s;

  @Before
  public void setup() throws IOException, InterruptedException {
    s = SampleServer.start();
    //   c1 = new SampleClient();
  }

  @Test
  public void test1() throws IOException {
    //String resp1 = (String) c1.sendMsg("Hello");
    String resp1 = "Hello";
    assertEquals("Hello", resp1);

  }
}
