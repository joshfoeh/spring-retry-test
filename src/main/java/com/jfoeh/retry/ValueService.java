package com.jfoeh.retry;

import org.springframework.stereotype.Service;

@Service
public class ValueService {

  public String getSuccessfulValue() {
    return "success";
  }

  public String getFailedValue() {
    return "failure";
  }

}
