/*
 * MongoConnectionFactory.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.houseOnline.model.MongoServer;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;

/**
 * <p>ClassName: MongoConnectionFactory</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 22, 2013
 */
public class MongoConnectionFactory {

  private static MongoClient mc = null;
  private static String dbname = null;

  static {
    try {
      if (ApplicationConfig.getInstance().getMongoServerCount() > 0) {
        List<ServerAddress> saLst = new ArrayList<ServerAddress>();
        for (MongoServer ms : ApplicationConfig.getInstance().getMongoServer()) {
          saLst.add(new ServerAddress(ms.getHost(), ms.getPort()));
        }
        mc = new MongoClient(saLst);

        dbname = ApplicationConfig.getInstance().getMongoDBName();
        if (StringUtils.isBlank(dbname)) {
          throw new Exception("DB name can not be blank");
        }
      }
    } catch (Exception e) {
      LoggerFactory.getSystemLogger().error("Failed in MongoClientFactory static block", e);
      throw new RuntimeException(e);
    }
  }

  /**
   * 返回MongoDB数据库连接
   * @return
   */
  public static DB getConnection() {
    return mc.getDB(dbname);
  }
}
