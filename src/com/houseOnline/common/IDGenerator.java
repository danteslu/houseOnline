/*
 * IDGenerator.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <p>ClassName: IDGenerator</p>
 * 
 * <p>Abstract:</p>
 * <p>分布式ID生成器，由日期+秒+应用标识+进程标识+自增计数器组成，为缩短ID的长度，转化为62进制。</p>
 * <ul>
 * <li>为了减少日期的长度，需要指定一个开始日期，而没必要从1970-01-01开始算起，初始为两个62进制字符，也就是说在62*62=3844天后(约10年)，才会升级到3位。</li>
 * <li>每日共有个24*60*60/30=2880个30秒，可用两个62进制字符。</li> 
 * <li>应用标识确保了不同应用产生不同的ID，初始为一个62进制字符，至少支持62个并行应用。</li>
 * <li>进程标识确保同一个应用多次启动后使用不同的ID，初始为一个62进制字符，通过一个临时文件确保过去一个年内不会出现重复的日期+进程标识的组合。</li>
 * <li>自增计数器，确保每30秒内产生的ID的唯一性，初始为两个62进制，每天重置。</li>
 * </ul> 
 * <p>因此，ID最短为8位，产生重复ID的情况主要有：</p>
 * <ul>
 * <li>系统时间正常的情况下，进程标识历史记录文件不存在，然后应用在30秒内重启成功，前后两次进程标识相同。</li>
 * <li>系统时间被调回到某个过去时间，进程标识历史记录文件不存在。</li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 16, 2013
 */
public class IDGenerator {

  /*
   * Constants
   */

  private final static String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  private final static String FILENAME_PROCESS_PROPERTIES = "process.properites";
  private final static String SEPERATOR = ",";

  // 基准日期，每个项目根据自己的时间设置一个值即可，这样无需从1970-01-01开始算起，以减少字符串长度
  private final static String START_DATE = "2013-01-01";

  /*
   * Fields
   */

  // 当前web应用的标识，不同的应用必须不一样，尤其是同一部服务器上的多个应用，否则就有可能生成相同的id
  private static int code = 0;

  // 当前应用的进程标识，每次重启后尽量确保不一致
  private static int process = 0;

  // 自增计数器，每次重启应用后从0开始
  private static AtomicInteger sequence = new AtomicInteger(0);

  // 基准日期
  private static Date startDate = null;
  private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

  // 
  private static Properties prop = null;

  static {
    FileOutputStream fos = null;
    try {
      LoggerFactory.getSystemLogger().info("Try to initialize IDGenerator");

      code = ApplicationConfig.getInstance().getCode();
      startDate = sdf.parse(START_DATE);

      Random random = new java.util.Random();
      Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
      String todayStr = sdf.format(calendar.getTime());

      String path = ApplicationConfig.getInstance().getDataPath();
      if (StringUtils.isBlank(path)) {
        throw new Exception("Need to setup data path in application.xml to store the process history");
      }
      File f = new File(path + FILENAME_PROCESS_PROPERTIES);
      if (!f.exists()) {
        f.createNewFile();
      }

      // 生成一个随机的进程标识
      process = random.nextInt(ALPHABET.length());

      // 检测该进程标识的使用情况
      prop = new Properties();
      prop.load(new FileInputStream(f)); // ISO 8859-1

      String usedStr = prop.getProperty(todayStr);
      if (usedStr == null) {
        // 如果不存在进程标识历史记录文件或者没有当天的记录，则随机生成一个        
        prop.setProperty(todayStr, String.valueOf(process));
      } else {
        // 如果已经存在当天的记录，则选择一个不同的进程标识
        String[] used = usedStr.split(SEPERATOR);
        int bits = 1;
        while (ArrayUtils.indexOf(used, String.valueOf(process)) >= 0) { // 伪随机数算法导致前后两次生成的数字会尽量不同，因此处需要循环的次数很少
          if (used.length >= (int) Math.pow(ALPHABET.length(), bits)) { // 如果一位已经用完
            bits++;
          }
          LoggerFactory.getSystemLogger().info("Process id " + process + " was used before");
          process = random.nextInt((int) Math.pow(ALPHABET.length(), bits));
        }

        String[] newUsed = (String[]) ArrayUtils.add(used, String.valueOf(process));
        prop.setProperty(todayStr, StringUtils.join(newUsed, SEPERATOR));
      }

      LoggerFactory.getSystemLogger().info("Current code is " + code + " and process id " + process);

      // 清除一年前的记录
      String oneYearAgo = sdf.format(new Date(calendar.getTimeInMillis() - 356 * 24 * 60 * 60));
      Set<String> dates = prop.stringPropertyNames();
      for (Object date : dates) {
        if (oneYearAgo.compareTo((String) date) > 0) {
          prop.remove(date);
          LoggerFactory.getSystemLogger().info("Try to remove the date from process history " + date);
        }
      }

      // 保存
      fos = new FileOutputStream(f);
      prop.store(fos, null);

    } catch (Exception e) {
      LoggerFactory.getSystemLogger().error("Failed in IDGenerator's static block", e);
      throw new RuntimeException(e);
    } finally {
      if (fos != null) {
        try {
          fos.close();
        } catch (IOException e) {
          LoggerFactory.getSystemLogger().error("Failed in close fileoutputstream", e);
        }
        fos = null;
      }
    }
  }

  /**
   * 返回一个ID
   * @return
   */
  public static String next() {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    Date now = calendar.getTime();
    String nowStr = sdf.format(now);

    long dateDiff = (now.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24);
    String datePiece = hash(dateDiff);

    int hour = calendar.get(Calendar.HOUR_OF_DAY);
    int minute = calendar.get(Calendar.MINUTE);
    int second = calendar.get(Calendar.SECOND);
    int every30second = (hour * 60 * 60 + minute * 60 + second) / 30;
    String secondPiece = hash(every30second);

    String codePiece = hash(code);

    String processPiece = hash(process);

    // 检测到日期发生改变时，重置sequence并保存进程历史记录
    if (!prop.containsKey(nowStr)) {
      LoggerFactory.getSystemLogger().info("Sequence reset");
      sequence.set(0);

      synchronized (IDGenerator.class) {
        FileOutputStream fos = null;
        try {
          File f = new File(ApplicationConfig.getInstance().getDataPath() + FILENAME_PROCESS_PROPERTIES);
          prop.setProperty(nowStr, String.valueOf(process));
          fos = new FileOutputStream(f);
          prop.store(fos, null);
        } catch (Exception e) {
          LoggerFactory.getSystemLogger().error("Failed in next", e);
        } finally {
          if (fos != null) {
            try {
              fos.close();
            } catch (IOException e) {
              LoggerFactory.getSystemLogger().error("Failed in close fileoutputstream", e);
            }
            fos = null;
          }
        }
      }
    }

    String sequencePiece = hash(sequence.incrementAndGet());
    if (sequencePiece.length() < 2) {
      sequencePiece = "0" + sequencePiece;
    }

    return datePiece + secondPiece + codePiece + processPiece + sequencePiece;

  }

  public static String hash(long number) {
    String hash = "";
    while (number > 0) {
      hash = ALPHABET.charAt((int) (number % ALPHABET.length())) + hash;
      number = number / ALPHABET.length();
    }
    return hash;
  }

}
