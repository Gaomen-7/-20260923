package com.gec.config;

import com.gec.controller.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public void handleException(Exception e, HttpServletResponse resp) {
    e.printStackTrace();
    R.err(e).write(resp);
  }

}
