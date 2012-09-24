package com.dulvac.jerry;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestListenerConfiguration {
  private HierarchicalConfiguration config = null;
  private static final Logger logger = LoggerFactory.getLogger(RequestListenerConfiguration.class.getName());

  private static final int PORT = 8080;
  private static final String FILES_ROOT = "./";
  private static final int HTTP_CONNECTION_TIMEOUT = 2000;
  private static final int HTTP_SOCKET_TIMEOUT = 2000;
  private static final int HTTP_SOCKET_BUFFER_SIZE = 8 * 1024;
  private static final int WORKERS_CORE_POOL_SIZE = 100;
  private static final int WORKERS_MAX_POOL_SIZE = 2000;
  private static final long WORKERS_KEEP_ALIVE_TIME = 60;
  private static final int WORKERS_QUEUE_SIZE = 500;

  private static final String HTTP_CONNECTION_TIMEOUT_NAME = "http.connection_timeout";
  private static final String HTTP_SOCKET_TIMEOUT_NAME = ".http.socket_timeout";
  private static final String HTTP_SOCKET_BUFFER_SIZE_NAME = "http.socket_buffer_size";
  private static final String WORKERS_CORE_POOL_SIZE_NAME = "workers.core_pool_size";
  private static final String WORKERS_MAX_POOL_SIZE_NAME = "workers.max_pool_size";
  private static final String WORKERS_KEEP_ALIVE_TIME_NAME = "workers.keep_alive_time";
  private static final String WORKERS_QUEUE_SIZE_NAME = "workers.queue_size";
  private static final String PORT_NAME = "port";
  private static final String FILES_ROOT_NAME = "filesRoot";

  public RequestListenerConfiguration(HierarchicalConfiguration config) {
    this.config = config;
  }
  
  public String getListenerId() {
    return config.getString("name");
  }
  
  public int getListenPort() {
    return config.getInt(PORT_NAME, PORT);
  }

  public String getFilesRoot() {
    return config.getString(FILES_ROOT_NAME, FILES_ROOT);
  }

  public int getConnectionTimeout() {
    return config.getInt(HTTP_CONNECTION_TIMEOUT_NAME, HTTP_CONNECTION_TIMEOUT);
  }

  public int getSocketTimeout() {
    return config.getInt(HTTP_SOCKET_TIMEOUT_NAME, HTTP_SOCKET_TIMEOUT);
  }
  public int getSocketBufferSize() {
    return config.getInt(HTTP_SOCKET_BUFFER_SIZE_NAME, HTTP_SOCKET_BUFFER_SIZE);
  }

  public int getWorkersCorePoolSize() {
    return config.getInt(WORKERS_CORE_POOL_SIZE_NAME, WORKERS_CORE_POOL_SIZE);
  }

  public int getWorkersMaxPoolSize() {
    return config.getInt(WORKERS_MAX_POOL_SIZE_NAME, WORKERS_MAX_POOL_SIZE);
  }

  public long getWorkersKeepAliveTime() {
    return config.getLong(WORKERS_KEEP_ALIVE_TIME_NAME, WORKERS_KEEP_ALIVE_TIME);
  }

  public int getWorkersQueueSize() {
    return config.getInt(WORKERS_QUEUE_SIZE_NAME, WORKERS_QUEUE_SIZE);
  }

}
