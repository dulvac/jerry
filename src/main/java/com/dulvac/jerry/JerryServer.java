package com.dulvac.jerry;

/**
 * JerryServer - Simple web server with thread pooling
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main class
 */
public class JerryServer {
  private static final Logger logger = LoggerFactory.getLogger(JerryServer.class.getName());
  public final String filesRoot;
  public final int port;

  public JerryServer(String filesRoot, int port) {
    this.filesRoot = filesRoot;
    this.port = port;
  }

  /**
   * Start the webserver
   * @param port The port on which to listen for inccoming requests
   * @throws IOException
   */
  // TODO: Add port and document root in external config. Also, allow for multiple listeners on different ports
  public void start(int port) throws IOException {
    Thread rl = new Thread(new RequestListener(port, this.filesRoot, 2000, 2000, 8 * 1024, 500));
    rl.setName("listener-" + port);
    rl.setDaemon(false);
    rl.start();
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      // TODO: jcommander or replace with configuration
      System.err.println("Usage: JerryServer document_root [port]");
      System.exit(1);
    }
    int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8080;
    JerryServer js = new JerryServer(args[0], port);
    try {
      logger.info("Starting JerryServer...");
      js.start(port);
    } catch (Exception ex) {
      logger.error("Error starting server ", ex);
    }
  }
}
