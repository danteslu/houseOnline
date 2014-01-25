/*
 * AbstractRobot.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.robot;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * <p>ClassName: AbstractRobot</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public abstract class AbstractRobot implements Job {

  protected Logger logger = Logger.getLogger(this.getClass());

  protected Map<String, String> parms = new HashMap<String, String>();

  /*
   * (non-Javadoc)
   * 
   * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
   */
  public void execute(JobExecutionContext context) throws JobExecutionException {
    try {
      init(context);
      run();
    } catch (Throwable e) {
      logger.error("Encounter unexpected error", e);
      JobExecutionException e2 = new JobExecutionException(e);
      // Quartz will automatically unschedule
      // all triggers associated with this job
      // so that it does not run again
      e2.setUnscheduleAllTriggers(true);
      throw e2;
    }
  }

  public abstract void run() throws Exception;

  protected void init(JobExecutionContext context) {
    JobDataMap jobData = context.getJobDetail().getJobDataMap();
    for (String key : jobData.getKeys()) {
      // Only support String key-value pairs
      if (jobData.get(key) instanceof String) {
        parms.put(key, (String) jobData.get(key));
      }
    }
  }

  public String getParm(String key) {
    return this.parms.get(key);
  }

  public void setParm(String key, String value) {
    this.parms.put(key, value);
  }
}
