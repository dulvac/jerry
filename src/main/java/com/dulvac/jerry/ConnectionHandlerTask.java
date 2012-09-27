package com.dulvac.jerry;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Handles an HTTP client connection
 *
 */
public class ConnectionHandlerTask implements Runnable {
  private static final Logger logger = LoggerFactory.getLogger(ConnectionHandlerTask.class.getName());
  private final HttpService httpservice;
  private final HttpServerConnection conn;
  private final String clientAddress;

  /**
   * Constructor
   * @param httpservice 
   * @param conn
   */
  public ConnectionHandlerTask(final HttpService httpservice, final HttpServerConnection conn) {
    super();
    this.httpservice = httpservice;
    this.conn = conn;
    clientAddress = ((HttpInetConnection)conn).getRemoteAddress().getHostAddress();
  }

  public void run() {
    logger.debug("Running new ConnectionHandlerTask thread");
    HttpContext context = new BasicHttpContext(null);
    try {
      while (!Thread.interrupted() && this.conn.isOpen()) {
        this.httpservice.handleRequest(this.conn, context);
      }
    } catch (ConnectionClosedException ex) {
      logger.info("Client ({}) closed connection", clientAddress);
    } catch (SocketTimeoutException ex){
      // thrown when keep-alive period expires; configured in httpParams when creating HttpServerConnection
      logger.info("Socket timed out for {}", clientAddress);
    } catch (IOException ex) {
      logger.error("I/O error: ", ex);
    } catch (HttpException ex) {
      logger.error("Unrecoverable HTTP protocol violation: ", ex);
    } finally {
      try {
        this.conn.shutdown();
      } catch (IOException ex) {
        logger.error("Error shutting down connection ", ex);
      }
    }
  }
}
