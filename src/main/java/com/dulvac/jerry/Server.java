package com.dulvac.jerry;

/**
 * Server - Simple web server with thread pooling
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Server {
  private static final Logger logger = LoggerFactory.getLogger(Server.class.getName());
  public final String filesRoot;
  public final int port;

  public Server(String filesRoot, int port) {
    this.filesRoot = filesRoot;
    this.port = port;
  }

  public void start(int port) throws IOException {
    Thread rl = new Thread(new RequestListener(port, this.filesRoot));
    rl.setName("listener-" + port);
    rl.setDaemon(false);
    rl.start();
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      // TODO: jcommander or replace with configuration
      System.err.println("Usage: Server document_root [port]");
      System.exit(1);
    }
    int port = (args.length >= 2) ? Integer.parseInt(args[1]) : 8080;
    Server js = new Server(args[0], port);
    try {
      logger.info("Starting JerryServer...");
      js.start(port);
    } catch (Exception ex) {
      logger.error("Error starting server ", ex);
    }
  }
}
