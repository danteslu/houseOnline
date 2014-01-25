/*
 * DailyRobot.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.robot;

/**
 * <p>ClassName: DailyRobot</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class DailyRobot extends AbstractRobot {

  /* (non-Javadoc)
   * @see com.gr.online.robot.AbstractRobot#run()
   */
  @Override
  public void run() throws Exception {
    logger.info(getParm(RobotEngine.PARAM_ROBOT_NAME) + " is running.");
    long start = System.currentTimeMillis();

    // TODO: Implement robot logic

    logger.info(getParm(RobotEngine.PARAM_ROBOT_NAME) + " ends " + (System.currentTimeMillis() - start) + "ms");
  }
}
