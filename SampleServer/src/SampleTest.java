import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;


public class SampleTest {
  SampleClient c1;
  SampleServer s;

  @Test
  public void test1() throws IOException {
    String resp1 = (String) c1.sendMsg("Hello");
    assertEquals("Hello", resp1);

  }
}
