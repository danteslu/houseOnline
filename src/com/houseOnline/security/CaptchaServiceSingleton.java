/*
 * CaptchaServiceSingleton.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.security;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.houseOnline.common.LoggerFactory;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.ImageCaptcha;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * <p>ClassName: CaptchaServiceSingleton</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class CaptchaServiceSingleton extends ListImageCaptchaEngine {
  private static CaptchaServiceSingleton instance = new CaptchaServiceSingleton();
  private static Logger log = LoggerFactory.getSystemLogger();
  ImageCaptcha imageCaptcha = null;

  public static CaptchaServiceSingleton getInstance() {
    return instance;
  }

  /** 
   * Private constructor to prevent instantiation 
   */
  private CaptchaServiceSingleton() {
  }

  protected void buildInitialFactories() {
    // 随机生成的字符
    WordGenerator wgen = new RandomWordGenerator("abcdefghijklmnopqrstuvwxyz123456789");
    RandomRangeColorGenerator cgen = new RandomRangeColorGenerator(new int[] { 0, 100 }, new int[] { 0, 100 },
        new int[] { 0, 100 });
    // 文字显示的个数
    TextPaster textPaster = new RandomTextPaster(new Integer(4), new Integer(4), cgen, true);
    // 图片的大小
    BackgroundGenerator backgroundGenerator = new UniColorBackgroundGenerator(new Integer(150), new Integer(50));
    // 字体格式
    Font[] fontsList = new Font[] { new Font("Arial", 0, 10), new Font("Tahoma", 0, 10), new Font("Verdana", 0, 10), };
    // 文字的大小
    FontGenerator fontGenerator = new RandomFontGenerator(new Integer(15), new Integer(30), fontsList);

    WordToImage wordToImage = new ComposedWordToImage(fontGenerator, backgroundGenerator, textPaster);
    this.addFactory(new GimpyFactory(wgen, wordToImage));
  }

  /** 
   * Write the captcha image of current user to the servlet response 
   *  
   * @param request 
   *            HttpServletRequest 
   * @param response 
   *            HttpServletResponse 
   * @throws IOException 
   */
  public void writeCaptchaImage(HttpServletRequest request, HttpServletResponse response) throws IOException {

    imageCaptcha = getNextImageCaptcha();
    HttpSession session = request.getSession();
    session.setAttribute("imageCaptcha", imageCaptcha);
    BufferedImage image = (BufferedImage) imageCaptcha.getChallenge();

    OutputStream outputStream = null;
    try {
      outputStream = response.getOutputStream();
      // render the captcha challenge as a JPEG image in the response  
      response.setHeader("Cache-Control", "no-store");
      response.setHeader("Pragma", "no-cache");
      response.setDateHeader("Expires", 0);
      response.setContentType("image/jpeg");
      JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(outputStream);
      encoder.encode(image);
      outputStream.flush();
      outputStream.close();
      outputStream = null;// no close twice  
    } catch (IOException ex) {
      log.error("error in writeCaptchaImage", ex);
      throw ex;
    } finally {
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException ex) {
        }
      }
      imageCaptcha.disposeChallenge();
    }
  }

  public boolean validateCaptchaResponse(String validateCode, HttpSession session) {
    boolean flag = true;
    try {
      imageCaptcha = (ImageCaptcha) session.getAttribute("imageCaptcha");
      if (imageCaptcha == null) {
        log.info("validateCaptchaResponse returned false due to imageCaptcha is null");
        flag = false;
      }
      //validateCode = validateCode.toLowerCase();// use upper case for  
      validateCode = validateCode.toLowerCase();// use upper case for  
      // easier usage  
      flag = (imageCaptcha.validateResponse(validateCode)).booleanValue();
      session.removeAttribute("imageCaptcha");
      return flag;
    } catch (Exception ex) {
      log.error("error in validateCaptchaResponse", ex);
      return false;
    }
  }
}
