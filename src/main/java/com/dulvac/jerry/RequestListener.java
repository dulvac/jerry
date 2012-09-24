package com.dulvac.jerry;

import com.dulvac.jerry.handlers.SimpleFileHandler;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.http.HttpResponseInterceptor;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.ResponseConnControl;
import org.apache.http.protocol.ResponseContent;
import org.apache.http.protocol.ResponseDate;
import org.apache.http.protocol.ResponseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request Listener thread
 * Accepts connections from clients and sends the job to its {@link ConnectionHandlerPool} worker pool to be processed
 */
public class RequestListener implements Runnable {
  
  private static final Logger logger = LoggerFactory.getLogger(RequestListener.class.getName());
  protected final ServerSocket serverSocket;
  private final Map<String, HttpRequestHandler> handlers;
  private final HttpService httpService;
  private final HttpParams httpParams;

  // each request listener should have its own pool; TODO: is this best?
  protected final ExecutorService handlerPool;
  private final String listenerId;
  private int listenPort;

  public String getListenerId() {
    return listenerId;
  }

  public int getListenPort() {
    return listenPort;
  }

  public RequestListener(RequestListenerConfiguration config) throws IOException {
    listenPort = config.getListenPort();
    String filesRoot = config.getFilesRoot();
    serverSocket = new ServerSocket(listenPort);
    listenerId = config.getListenerId() + "/" + listenPort;
    handlers = new HashMap<String, HttpRequestHandler>();
    handlers.put("*", new SimpleFileHandler(filesRoot));

    // Build parameters object for httpService
    this.httpParams = new BasicHttpParams();
    this.httpParams.setParameter(CoreProtocolPNames.ORIGIN_SERVER, "JerryServer/1.0") /* 'Server' header */
      .setBooleanParameter(CoreConnectionPNames.TCP_NODELAY, true) /* Don't buffer network data */
      .setBooleanParameter(CoreConnectionPNames.STALE_CONNECTION_CHECK, false) /* Disable this, adds overhead */
      .setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, config.getConnectionTimeout())
      .setIntParameter(CoreConnectionPNames.SO_TIMEOUT, config.getSocketTimeout()) /* important to tweak keep-alive */
      .setIntParameter(CoreConnectionPNames.SOCKET_BUFFER_SIZE, config.getSocketBufferSize());

    // Set up the HTTP protocol processor
    // This allows multiple interceptors to process an outgoing response incrementally (e.g. Adding headers)
    HttpProcessor httpProcessor = new ImmutableHttpProcessor(
      new HttpResponseInterceptor[]{new ResponseDate(), /* Adds date header */
                                    new ResponseServer(), /* Add JerryServer header */
                                    new ResponseContent(), /* Writes Content-Length and Transfer-Encoding headers */
                                    new ResponseConnControl() /* Connection header; keep-alive for HTTP/1.0 */});

    // Register handlers
    HttpRequestHandlerRegistry httpRegistry = new HttpRequestHandlerRegistry();
    httpRegistry.setHandlers(this.handlers);

    // Instantiate the HttpServiceObject; it wraps up the processor and the registry
    this.httpService = new HttpService(httpProcessor, new DefaultConnectionReuseStrategy(),
                                       new DefaultHttpResponseFactory(), httpRegistry, this.httpParams);

    // Initialize worker pool
    this.handlerPool = new ConnectionHandlerPoolBuilder()
      .withPoolSize(config.getWorkersCorePoolSize(), config.getWorkersMaxPoolSize())
      .withKeepAliveTime(config.getWorkersKeepAliveTime())
      .withWorkQueueSize(config.getWorkersQueueSize())
      .withThreadNamePrefix(listenerId)
      .build();

  }
  
  public void run() {
    logger.info("Listening on port {}", this.serverSocket.getLocalPort());
    // listen forever
    while (!Thread.interrupted()) {
      try {
        // Accept client connections
        Socket socket = this.serverSocket.accept();
        DefaultHttpServerConnection conn = new DefaultHttpServerConnection();
        logger.info("Incoming connection from {}", socket.getInetAddress().getHostAddress());
        conn.bind(socket, this.httpParams);

        // send job to handler pool
        this.handlerPool.submit(new ConnectionHandlerTask(this.httpService, conn));

      } catch (InterruptedIOException ex) {
        break;
      } catch (IOException e) {
        logger.error("Error initialising connection thread: ",  e);
        break;
      }
    }

    // close pool when exiting this loop; threads are daemon threads, but like being thorough
    this.handlerPool.shutdown();
  }

}
