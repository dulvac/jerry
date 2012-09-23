package com.dulvac.jerry;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Builder class for {@link ConnectionHandlerPool}
 * default core pool size: 100
 * default maximum pool size: 2000
 * default keep alive time: 1 second
 * default work queue size: none (uses an unbounded {@link LinkedBlockingQueue})
 */
public class ConnectionHandlerPoolBuilder {
  private int corePoolSize = 100;
  private int maximumPoolSize = 2000;
  private long keepAliveTime = 60;
  private static final TimeUnit unit = TimeUnit.SECONDS;
  private BlockingQueue<Runnable> workQueue;
  private int workQueueSize;
  private String threadNamePrefix = "ConnectionHandlerPool";

  /**
   *
   * @param corePoolSize The initial size of the {@link ConnectionHandlerPool}
   * @param maximumPoolSize The maximum size of the @link ConnectionHandlerPool}
   * @return this (@link ConnectionHandlerPoolBuilder) object
   */
  public ConnectionHandlerPoolBuilder withPoolSize(int corePoolSize, int maximumPoolSize) {
    this.corePoolSize = corePoolSize;
    this.maximumPoolSize = maximumPoolSize;
    return this;
  }

  /**
   *
   * @param seconds Number of seconds to keep idle threads before deleting them from the {@link ConnectionHandlerPool}
   * @return this (@link ConnectionHandlerPoolBuilder) object
   */
  public ConnectionHandlerPoolBuilder withKeepAliveTime(long seconds) {
    this.keepAliveTime = seconds;
    return this;
  }

  /**
   * Add a prefix to the name of the worker threads in the {@link ConnectionHandlerPool}
   * @param prefix The thread name prefix
   * @return
   */
  public ConnectionHandlerPoolBuilder withThreadNamePrefix(String prefix) {
    this.threadNamePrefix = prefix;
    return this;
  }

  /**
   *
   * @param workQueueSize The size of the {@link ConnectionHandlerPool} work queue
   * @return
   */
  public ConnectionHandlerPoolBuilder withWorkQueueSize(int workQueueSize) {
    this.workQueueSize = workQueueSize;
    workQueue = new ArrayBlockingQueue<Runnable>(this.workQueueSize);
    return this;
  }

  /**
   * Builds a {@link ConnectionHandlerPool}
   * @return new {@link ConnectionHandlerPool}
   */
  public ConnectionHandlerPool build() {
    if (null == workQueue) {
      workQueue = new LinkedBlockingQueue<Runnable>();
    }
    return new ConnectionHandlerPool(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadNamePrefix);
  }

}
