/*
 * CaptchaServiceSingleton.java
 * 
 * All Rights Reserved.
 */
package com.houseOnline.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.octo.captcha.service.CaptchaServiceException;

/**
 * <p>ClassName: ImageCaptchaServlet</p>
 * 
 * <p>Abstract:</p>
 * <ul>
 * <li></li>
 * </ul>
 *
 * @author Samuel Feng
 * @since Dec 14, 2013
 */
public class ImageCaptchaServlet extends HttpServlet {

  private static final long serialVersionUID = 8046018851215593895L;

  @Override
  protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
      throws ServletException, IOException {
    try {
      CaptchaServiceSingleton.getInstance().writeCaptchaImage(httpServletRequest, httpServletResponse);
    } catch (IllegalArgumentException e) {
      httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    } catch (CaptchaServiceException e) {
      httpServletResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      return;
    }
  }
}
