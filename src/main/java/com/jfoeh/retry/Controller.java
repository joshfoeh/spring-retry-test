package com.jfoeh.retry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  private Retry retry;

  @Autowired
  public Controller(Retry retry) {
    this.retry = retry;
  }

  @GetMapping("/retry/proxy")
  public String getProxiedRetry() {
    System.out.println("Doing proxied retry");
    String proxiedVal = retry.getValue(true);
    System.out.println("Proxied response: " + proxiedVal);

    return proxiedVal;
  }

  @GetMapping("/retry/direct")
  public String getDirectRetry() {
    System.out.println("Doing direct retry");
    String directVal = retry.getValueWithRetry(false);
    System.out.println("Direct response: " + directVal);

    return directVal;
  }

}
