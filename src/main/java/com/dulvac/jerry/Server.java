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

  public Server(String filesRoot) {
    this.filesRoot = filesRoot;
  }

  public void start(int port) throws IOException {
    Thread rl = new Thread(new RequestListener(port, this.filesRoot));
    rl.setDaemon(false);
    rl.start();
  }

  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println("Must provide the document root directory. Exiting.");
      System.exit(1);
    }
    Server js = new Server(args[0]);
    try {
      logger.info("Starting jerry webserver...");
      js.start(8080);
    } catch (Exception ex) {
      logger.error("Error starting server ", ex);
    }
  }
}
