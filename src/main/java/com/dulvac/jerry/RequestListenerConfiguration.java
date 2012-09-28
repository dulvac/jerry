package com.dulvac.jerry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestListenerConfiguration {
  private HierarchicalConfiguration config = null;
  private static final Logger logger = LoggerFactory.getLogger(RequestListenerConfiguration.class.getName());

  public static final int PORT = 8080;
  public static final String FILES_ROOT = "./";
  public static final int HTTP_CONNECTION_TIMEOUT = 2000;
  public static final int HTTP_SOCKET_TIMEOUT = 2000;
  public static final int HTTP_SOCKET_BUFFER_SIZE = 8 * 1024;
  public static final int WORKERS_CORE_POOL_SIZE = 100;
  public static final int WORKERS_MAX_POOL_SIZE = 2000;
  public static final long WORKERS_KEEP_ALIVE_TIME = 60;
  public static final int WORKERS_QUEUE_SIZE = 500;

  private static final String HTTP_CONNECTION_TIMEOUT_NAME = "http.connection_timeout";
  private static final String HTTP_SOCKET_TIMEOUT_NAME = ".http.socket_timeout";
  private static final String HTTP_SOCKET_BUFFER_SIZE_NAME = "http.socket_buffer_size";
  private static final String WORKERS_CORE_POOL_SIZE_NAME = "workers.core_pool_size";
  private static final String WORKERS_MAX_POOL_SIZE_NAME = "workers.max_pool_size";
  private static final String WORKERS_KEEP_ALIVE_TIME_NAME = "workers.keep_alive_time";
  private static final String WORKERS_QUEUE_SIZE_NAME = "workers.queue_size";
  private static final String PORT_NAME = "port";
  private static final String FILES_ROOT_NAME = "filesRoot";

  /**
   *
   * @param config An underlying configuration object
   */
  public RequestListenerConfiguration(HierarchicalConfiguration config) {
    this.config = config;
  }

  /**
   *
   * @return The listener name
   */
  public String getListenerId() {
    return config.getString("name");
  }

  /**
   *
   * @return The port on which the listener listens for new requests
   */
  public int getListenPort() {
    return config.getInt(PORT_NAME, PORT);
  }

  /**
   *
   * @return The listener document root
   */
  public String getFilesRoot() {
    return config.getString(FILES_ROOT_NAME, FILES_ROOT);
  }

  /**
   *
   * @return The timeout in milliseconds after which a new establishing connection times out
   */
  public int getConnectionTimeout() {
    return config.getInt(HTTP_CONNECTION_TIMEOUT_NAME, HTTP_CONNECTION_TIMEOUT);
  }

  /**
   *
   * @return The timeout in milliseconds after which an established connection times out
   */
  public int getSocketTimeout() {
    return config.getInt(HTTP_SOCKET_TIMEOUT_NAME, HTTP_SOCKET_TIMEOUT);
  }

  /**
   *
   * @return The buffer size of the TCP socket
   */
  public int getSocketBufferSize() {
    return config.getInt(HTTP_SOCKET_BUFFER_SIZE_NAME, HTTP_SOCKET_BUFFER_SIZE);
  }

  /**
   *
   * @return The listener's initial number of worker threads
   */
  public int getWorkersCorePoolSize() {
    return config.getInt(WORKERS_CORE_POOL_SIZE_NAME, WORKERS_CORE_POOL_SIZE);
  }

  /**
   *
   * @return The listener's maximum number of worker threads
   */
  public int getWorkersMaxPoolSize() {
    return config.getInt(WORKERS_MAX_POOL_SIZE_NAME, WORKERS_MAX_POOL_SIZE);
  }

  /**
   *
   * @return The HTTP keep-alive time, in seconds
   */
  public long getWorkersKeepAliveTime() {
    return config.getLong(WORKERS_KEEP_ALIVE_TIME_NAME, WORKERS_KEEP_ALIVE_TIME);
  }

  /**
   *
   * @return The size of the underlying unprocessed requests queue
   */
  public int getWorkersQueueSize() {
    return config.getInt(WORKERS_QUEUE_SIZE_NAME, WORKERS_QUEUE_SIZE);
  }

}
