package com.dulvac.jerry;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Custom ThreadPoolExecutor
 * Creates daemon threads
 */
public class ConnectionHandlerPool extends ThreadPoolExecutor {

  /**
   * Custom implementation of {@link ThreadFactory}
   * Creates daemon threads
   */
  public class ConnectionHandlerFactory implements ThreadFactory {
    private int counter = 0;
    private String prefix = "";

    /**
     *
     * @param prefix Prefix to use for thread names
     */
    public ConnectionHandlerFactory(String prefix) {
      this.prefix = prefix;
    }

    public Thread newThread(Runnable r) {
      Thread t = new Thread(r, prefix + "-" + counter++);
      t.setDaemon(true);
      return t;
    }
  }

  /**
   * Constructor
   *
   * @param corePoolSize
   * @param maximumPoolSize
   * @param keepAliveTime
   * @param unit
   * @param workQueue
   */
  public ConnectionHandlerPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                               BlockingQueue<Runnable> workQueue, String threadNamePrefix) {
    super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    ConnectionHandlerFactory factory = new ConnectionHandlerFactory(threadNamePrefix);
    this.setThreadFactory(factory);
  }

}
