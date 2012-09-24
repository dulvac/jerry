package com.dulvac.jerry;

/**
 * JerryServer - Simple web server with thread pooling
 *
 */

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class
 */
public class JerryServer {
  private static final Logger logger = LoggerFactory.getLogger(JerryServer.class.getName());
  private static final String CONFIG_FILENAME = "jerry.xml";
  private HierarchicalConfiguration config;
  public List<RequestListener> requestListeners;

  public JerryServer() {
    // Add cheap initialization logic here
    requestListeners = new ArrayList<RequestListener>();
  }

  public void init() {
    logger.info("Initializing server.");
    // Load server configuration
    try {
      config = new XMLConfiguration(CONFIG_FILENAME);
    } catch (ConfigurationException ex) {
      logger.error("Couldn't load configuration. Exiting.", ex);
      System.exit(1);
    }
    // Populate request listeners
    try {
      loadRequestListeners(config);
    } catch (IOException ex) {
      logger.error("Error loading request listeners. Exiting.", ex);
      System.exit(1);
    }
  }

  public void loadRequestListeners(HierarchicalConfiguration config) throws IOException {
    // get configuration node for each listener
    List<HierarchicalConfiguration> listenerConfigs = config.configurationsAt("listeners.listener");
    // for each listener configuration, build configuration wrapper and start thread
    for (HierarchicalConfiguration listenerConfig : listenerConfigs) {
      requestListeners.add(new RequestListener(new RequestListenerConfiguration(listenerConfig)));
    }
  }

  public void startRequestListener(RequestListener rl) {
    Thread rlThread = new Thread(rl);
    rlThread.setName(rl.getListenerId());
    rlThread.setDaemon(false);
    logger.info("Starting request listener {} on port {}", rl.getListenerId(), rl.getListenPort());
    rlThread.start();
  }


  /**
   * Start the webserver
   *
   * @throws IOException
   */
  public void start() throws IOException {
    // initialize
    init();
    // start all listeners
    for (RequestListener rl: requestListeners) {
      startRequestListener(rl);
    }
  }

  public static void main(String[] args) {
    JerryServer js = new JerryServer();
    try {
      logger.info("Starting JerryServer...");
      js.start();
    } catch (Exception ex) {
      logger.error("Error starting server ", ex);
    }
  }
}
