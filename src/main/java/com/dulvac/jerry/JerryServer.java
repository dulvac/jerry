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
import java.net.BindException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main class
 * <p>This is a server object configured with a number of requests listeners, which do the actual server jobs</p>
 */
public class JerryServer {
  private static final Logger logger = LoggerFactory.getLogger(JerryServer.class.getName());
  public static final String CONFIG_FILENAME = "jerry.xml";
  private HierarchicalConfiguration configuration;
  public List<RequestListener> requestListeners;
  private List<Thread> listenerThreads;

  /**
   * @return The underlying configuration object
   */
  public HierarchicalConfiguration getConfiguration() {
    return configuration;
  }

  /**
   * @param configuration The configuration object to be set
   */
  public void setConfiguration(HierarchicalConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Constructor
   * <p>This is lightweight. It does not do any configuration loading or complex initialization</p>
   */
  public JerryServer() {
    // Add cheap initialization logic here
    requestListeners = new ArrayList<RequestListener>();
    listenerThreads = new ArrayList<Thread>();
  }

  /**
   * Loads a configuration from an XML filename and configures all the listeners
   * <p>See {@see XMLConfiguration} for more details on the format</p>
   *
   * @param filename The XML filename to be loaded
   * @throws BindException if a configured port is already in use
   * @throws IOException   if the file doesn't exist or is invalid
   */
  public void loadConfiguration(String filename) throws BindException, IOException {
    try {
      HierarchicalConfiguration conf = new XMLConfiguration(filename);
      loadConfiguration(conf);
    } catch (ConfigurationException ex) {
      logger.error("Couldn't load configuration. Exiting.", ex);
      System.exit(1);
    }
  }

  /**
   * Loads a configuration from an XML filename and configures all the listeners
   * <p>It does the same as {@see loadConfiguration} but loads from the default filename @{see CONFIG_FILENAME}</p>
   *
   * @throws BindException if a configured port is already in use
   * @throws IOException   if the file doesn't exist or is invalid
   */
  public void loadConfiguration() throws BindException, IOException {
    loadConfiguration(CONFIG_FILENAME);
  }

  /**
   * Loads a configuration from {@param config} and configures all the listeners
   * <p>See {@see XMLConfiguration} for more details on the format</p>
   *
   * @param config A {@see HierarchicalConfiguration} object populated with all the values
   * @throws BindException if a configured port is already in use
   * @throws IOException   if the file doesn't exist or is invalid
   */
  public void loadConfiguration(HierarchicalConfiguration config) throws BindException, IOException {
    this.configuration = config;
    requestListeners = new ArrayList<RequestListener>();
    // get configuration node for each listener
    List<HierarchicalConfiguration> listenerConfigs = config.configurationsAt("listeners.listener");
    // for each listener configuration, build configuration wrapper and start thread
    for (HierarchicalConfiguration listenerConfig : listenerConfigs) {
      requestListeners.add(new RequestListener(new RequestListenerConfiguration(listenerConfig)));
    }
  }

  /**
   * Creates and starts a thread from {@param rl}
   * <p>Also adds the newly created thread to an internal list</p>
   *
   * @param rl
   */
  private void startRequestListener(RequestListener rl) {
    Thread rlThread = new Thread(rl);
    rlThread.setName(rl.getListenerId());
    rlThread.setDaemon(false);
    logger.info("Starting request listener {} on port {}", rl.getListenerId(), rl.getListenPort());
    rlThread.start();
    listenerThreads.add(rlThread);
  }

  /**
   * Start the webserver
   * Starts all listener threads
   */
  public void start() {
    // start all listeners
    for (RequestListener rl : requestListeners) {
      startRequestListener(rl);
    }
  }

  /**
   * Stop the webserver
   */
  public void stop() {
    // stop all listener threads
    for (Thread rlThread : listenerThreads) {
      // TODO: Wrap the thread in a class and call a stop() method that interrupts the underlying thread.
      rlThread.interrupt();
    }
  }

  public static void main(String[] args) {
    JerryServer js = new JerryServer();

    // load default configuration
    try {
      logger.info("Initializing server.");
      js.loadConfiguration();
    } catch (IOException ex) {
      logger.error("Error loading request listeners. Exiting.", ex);
      System.exit(1);
    }

    // start server (all listeners)
    try {
      logger.info("Starting JerryServer...");
      js.start();
    } catch (Exception ex) {
      logger.error("Error starting server ", ex);
    }
  }
}
