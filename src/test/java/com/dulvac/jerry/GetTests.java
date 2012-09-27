package com.dulvac.jerry;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.BindException;
import java.net.Socket;

public class GetTests {
  private File documentRoot;
  private JerryServer server;
  private HierarchicalConfiguration config;
  private int port = 18080;

  @BeforeClass(groups = {"functional", "real_server"})
  public void startServer() {
    // set up a document root
    documentRoot = Files.createTempDir();

    // create listeners configuration
    config = new HierarchicalConfiguration();
    config.addProperty("listeners.listener.port", Integer.toString(port));
    config.addProperty("listeners.listener.filesRoot", documentRoot.getAbsolutePath());
    // the values below could, in fact, be missing; there are defaults in RequestListenerConfiguration
    config.addProperty("jerry.listeners.listener.http.connection_timeout", "3000");
    config.addProperty("jerry.listeners.listener.http.socket_timeout", "3000");
    config.addProperty("jerry.listeners.listener.http.socket_buffer_size", "16384");
    config.addProperty("jerry.listeners.listener.workers.core_pool_size", "30");
    config.addProperty("jerry.listeners.listener.workers.max_pool_size", "500");
    config.addProperty("jerry.listeners.listener.workers.keep_alive_time", "5");
    config.addProperty("jerry.listeners.listener.workers.queue_size", "80");
    
    // create webserver
    server = new JerryServer();

    // Now try to bind the listener on an open port
    for (int i = 0; i < 10; i++) {
      try {
        // increment port number
        server.loadConfiguration(config);
        server.start();
        break;
      } catch (BindException e) {
        System.out.println("Couldn't start server on " + port + ". Trying " + port + 1);
        // increment port number
        config.setProperty("listeners.listener.port", Integer.toString(++port));
        continue;
      } catch (IOException e) {
        Assert.fail("Error loading configuration: ", e);
      }
    }
  }

  @AfterClass(groups = {"functional", "real_server"})
  public void oneTimeTearDown() {
    // stop the server (all listeners)
    server.stop();
  }

  @Test(groups = {"functional", "real_server"})
  public void testGetIndex() throws IOException {
    File index = new File(this.documentRoot, "index.html");
    index.createNewFile();
    final int fileSize = 100;
    String content = TestUtils.generateFileContent(fileSize);
    Files.write(content, index, Charsets.UTF_8);

    // Open a client socket and send a simple request
    final Socket socket = new Socket("localhost", this.port);
    OutputStream out = socket.getOutputStream();
    out.write("GET / HTTP/1.1\r\n".getBytes());
    out.write(("Host: localhost:" + this.port + "\r\n").getBytes());
    // this is necessary so we can get the response and not wait for timeout
    out.write("Connection: close\r\n".getBytes());
    out.write("\r\n".getBytes());
    out.flush();

    InputStream in = socket.getInputStream();
    String response = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
    System.out.println(this.documentRoot.getAbsolutePath());
    System.out.println("response=" + response);
    //Assert.assertTrue(response.indexOf("200 OK") > 0);
    Assert.assertEquals(response.substring(0, 15), "HTTP/1.1 200 OK");
    Assert.assertTrue(response.contains(content));
  }

  @Test(groups = {"functional", "real_server"})
  public void testResourceFile() throws IOException {
    File index = new File(this.documentRoot, "some page.html");
    index.createNewFile();
    final int fileSize = 100;
    String content = TestUtils.generateFileContent(fileSize);
    Files.write(content, index, Charsets.UTF_8);

    // Open a client socket and send a simple request
    final Socket socket = new Socket("localhost", this.port);
    OutputStream out = socket.getOutputStream();
    out.write("GET /some%20page.html HTTP/1.1\r\n".getBytes());
    out.write(("Host: localhost:" + this.port + "\r\n").getBytes());
    // this is necessary so we can get the response and not wait for timeout
    out.write("Connection: close\r\n".getBytes());
    out.write("\r\n".getBytes());
    out.flush();

    InputStream in = socket.getInputStream();
    String response = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
    System.out.println(this.documentRoot.getAbsolutePath());
    System.out.println("response=" + response);
    //Assert.assertTrue(response.indexOf("200 OK") > 0);
    Assert.assertEquals(response.substring(0, 15), "HTTP/1.1 200 OK");
    Assert.assertTrue(response.contains(content));
  }

  @Test(groups = {"functional", "real_server"})
  public void test404() throws IOException {
    // create file outside the 'www' document root
    String filePath = "newDir-" + System.currentTimeMillis();
    final File newDir = new File(documentRoot, filePath);
    newDir.mkdir(); // this should be empty

    filePath = filePath + "/doesntexist";

    // Open a client socket and send a simple request
    final Socket socket = new Socket("localhost", this.port);
    OutputStream out = socket.getOutputStream();
    out.write(("GET /" + filePath + " HTTP/1.1\r\n").getBytes());
    out.write("Host: localhost\r\n".getBytes());
    out.write("Connection: close\r\n".getBytes());
    out.write("\r\n".getBytes());
    out.flush();

    InputStream in = socket.getInputStream();
    String response = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
    System.out.println("response=" + response);
    Assert.assertEquals(response.substring(0, 12), "HTTP/1.1 404");
  }

  @Test(groups = {"functional", "real_server"})
  public void test404withIndex() throws IOException {
    String filePath = "newDir-" + System.currentTimeMillis();
    final File newDir = new File(documentRoot, filePath);
    newDir.mkdir(); // this should be empty

    // create adn write an index file
    File index = new File(newDir, "index.html");
    index.createNewFile();

    filePath = filePath + "/doesntexist";

    // Open a client socket and send a simple request
    final Socket socket = new Socket("localhost", this.port);
    OutputStream out = socket.getOutputStream();
    out.write(("GET /" + filePath + " HTTP/1.1\r\n").getBytes());
    out.write("Host: localhost\r\n".getBytes());
    out.write("Connection: close\r\n".getBytes());
    out.write("\r\n".getBytes());
    out.flush();

    InputStream in = socket.getInputStream();
    String response = CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
    System.out.println("response=" + response);
    Assert.assertEquals(response.substring(0, 12), "HTTP/1.1 404");
  }
}
