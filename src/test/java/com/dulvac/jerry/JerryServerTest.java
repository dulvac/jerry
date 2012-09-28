package com.dulvac.jerry;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.http.params.CoreConnectionPNames;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.BindException;

public class JerryServerTest {

  @BeforeClass
  public void oneTimeSetUp() {
    // one-time initialization code
  }

  @AfterClass
  public void oneTimeTearDown() {
    // one-time cleanup code
  }

  @BeforeMethod
  public void setUp() {
  }

  @AfterMethod
  public void tearDown() {
  }

  @Test
  public void testLoadConfiguration() {
    JerryServer js = new JerryServer();
    // create listeners configuration
    HierarchicalConfiguration config = new HierarchicalConfiguration();
    config.addProperty("listeners.listener.filesRoot", "/some/path");
    config.addProperty("jerry.listeners.listener.http.connection_timeout", "42");

    int port = 18090;
    config.addProperty("listeners.listener.port", "8080");
    for (int i = 0; i < 10; i++) {
      try {
        // increment port number
        js.loadConfiguration(config);
        break;
      } catch (BindException e) {
        System.out.println("Couldn't start server on " + port + ". Trying " + port+1);
        // increment port number
        config.setProperty("listeners.listener.port", Integer.toString(++port));
        continue;
      } catch (IOException e) {
        Assert.fail("Error loading configuration: ", e);
      }
    }

    Assert.assertTrue((null != js.requestListeners) && (js.requestListeners.size() == 1));
    RequestListener rl = js.requestListeners.get(0);
    Assert.assertEquals(rl.getListenPort(), port);
    Assert.assertEquals(rl.getHttpParams().getIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, -1), 42);
    Assert.assertEquals(rl.getHttpParams().getIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, -1),
                        RequestListenerConfiguration.HTTP_SOCKET_BUFFER_SIZE);
  }
}
