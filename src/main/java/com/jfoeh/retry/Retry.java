package com.jfoeh.retry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
public class Retry {

  private static final int MAX_ATTEMPTS = 4;
  private boolean hasTried = false;

  private ValueService valueService;

  @Autowired
  public Retry(ValueService valueService) {
    this.valueService = valueService;
  }

  public String getValue(boolean shouldRetry) {
    if (shouldRetry) {
      hasTried = false;
      return getValueWithRetry(false);
    }
    System.out.println("Returning without retrying");
    return valueService.getSuccessfulValue();
  }

  @Retryable(value = IllegalStateException.class, maxAttempts = MAX_ATTEMPTS)
  public String getValueWithRetry(boolean shouldFail) {
    System.out.println("In retry method");
    valueService.tryService();

    if (shouldFail) {
      throw new IllegalStateException();
    }

    if (!hasTried) {
      // Always retry once
      hasTried = true;
      throw new IllegalStateException();
    }

    return valueService.getSuccessfulValue();
  }

  @Recover
  public String recover(IllegalStateException e) {
    System.out.println("Recovering from failed retry");
    return valueService.getFailedValue();
  }

}
