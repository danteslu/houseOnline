/*
 * RobotEngine.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.robot;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SchedulerMetaData;
import org.quartz.impl.StdSchedulerFactory;

import com.houseOnline.common.ApplicationConfig;
import com.houseOnline.common.LoggerFactory;
import com.houseOnline.model.Param;
import com.houseOnline.model.Robot;

/**
 * <p>ClassName: RobotEngine</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class RobotEngine {
  /*
   * Constants
   */
  public static String ROBOT_GROUP_DEFAULT = "ROBOT_GROUP_DEFAULT";
  public static String PARAM_ROBOT_NAME = "ROBOT_NAME";

  /*
   * Singleton
   */
  private static RobotEngine factory = new RobotEngine();

  public static RobotEngine getInstance() {
    return factory;
  }

  /*
   * Private Fields
   */
  private List<Robot> robots = null;
  private Logger logger = LoggerFactory.getSystemLogger();
  private Scheduler sched = null;

  private RobotEngine() {
    init();
  }

  private void init() {
    robots = ApplicationConfig.getInstance().getRobot();
    if (robots.size() == 0) {
      logger.info("No Robot is defined");
    }
  }

  /**
   * This method invokes quartz's scheduler to start the robots defined in
   * robot.xml
   */
  public void start() {

    // First we must get a reference to a scheduler
    SchedulerFactory sf = new StdSchedulerFactory();
    try {
      sched = sf.getScheduler();
      logger.info("------- Robot Factory Scheduling Robots ----------------");
      for (Robot bean : robots) {
        JobDetail job = new JobDetail(bean.getName(), ROBOT_GROUP_DEFAULT, Class.forName(bean.getImplClass()));
        job.getJobDataMap().put(PARAM_ROBOT_NAME, bean.getName());
        for (Param p : bean.getParam()) {
          job.getJobDataMap().put(p.getName(), p.getValue());
        }

        CronTrigger trigger = new CronTrigger(bean.getName() + "_trigger", ROBOT_GROUP_DEFAULT, bean.getName(),
            ROBOT_GROUP_DEFAULT, bean.getCron());
        sched.addJob(job, true);
        Date ft = sched.scheduleJob(trigger);
        logger.info(job.getFullName() + " has been scheduled to run at: " + ft + " and repeat based on expression: "
            + trigger.getCronExpression());
      }
      sched.start();
      logger.info("------- Robot Factory Started Robot Scheduler ----------------");

    } catch (SchedulerException e) {
      logger.error("Failed when starting scheduler of Robot factory", e);
    } catch (ClassNotFoundException e) {
      logger.error("Failed when getting robot instance of Robot factory", e);
    } catch (ParseException e) {
      logger.error("Failed when parsing robot configuration of Robot factory", e);
    }
  }

  public void shutdown() {
    logger.info("------- Robot Factory Shutting Down ---------------------");
    if (sched != null) {
      // shut down the scheduler
      try {
        sched.shutdown(true);
        logger.info("------- Shutdown Complete -----------------");
        SchedulerMetaData metaData = sched.getMetaData();
        logger.info("Executed " + metaData.numJobsExecuted() + " jobs.");
      } catch (SchedulerException e) {
        logger.error("Can not shutdown Robot Factory", e);
      }
    } else {
      logger.info("------- Scheduler not started -----------------");
    }
  }

  public Scheduler getSched() {
    return sched;
  }
}
