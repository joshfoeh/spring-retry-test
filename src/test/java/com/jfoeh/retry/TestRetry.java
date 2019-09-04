package com.jfoeh.retry;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig(TestRetry.TestConfig.class)
public class TestRetry {

  @Configuration
  @EnableRetry
  public static class TestConfig {

    private ValueService configValueService;

    @Bean
    public ValueService getValueService() {
      if (configValueService == null) {
        configValueService = mock(ValueService.class);
      }
      return configValueService;
    }

    @Bean
    public Retry getRetry() {
      return new Retry(getValueService());
    }

  }

  @Autowired
  private Retry underTest;

  @Autowired
  private ValueService valueService;

  @BeforeEach
  public void setup() {
    reset(valueService);
  }

  @Test
  public void testProxiedNoRetrySuccess() {
    // Initialization
    String expected = "mockedSuccess";
    when(valueService.getSuccessfulValue()).thenReturn(expected);

    // Execution
    String actual = underTest.getValue(false);

    // Verification
    assertEquals(expected, actual);
    verify(valueService).getSuccessfulValue();
    verifyNoMoreInteractions(valueService);
  }

  @Test
  public void testDirectRetrySuccess() {
    // Initialization
    String expected = "mockedSuccess";
    when(valueService.getSuccessfulValue()).thenReturn(expected);

    // Execution
    String actual = underTest.getValueWithRetry(false);

    // Verification
    assertEquals(expected, actual);
    verify(valueService, times(2)).tryService();
    verify(valueService).getSuccessfulValue();
    verifyNoMoreInteractions(valueService);
  }

  @Test
  public void testDirectRetryFailure() {
    // Initialization
    String expected = "mockedFailure";
    when(valueService.getFailedValue()).thenReturn(expected);

    // Execution
    String actual = underTest.getValueWithRetry(true);

    // Verification
    assertEquals(expected, actual);
    verify(valueService, times(4)).tryService();
    verify(valueService).getFailedValue();
    verifyNoMoreInteractions(valueService);
  }


  /*
  The below test should pass, but it instead throws an IllegalStateException. The getValue() method should call the
  @Retryable getValueWithRetry() method which should then throw an exception and retry, however when
  getValueWithRetry() is called from a method in the same class, it does not use the AOP proxy and seems to call
  the method directly instead (which has no try/catch built in)
   */
  @Test
  public void testProxiedRetrySuccess() {
    // Initialization
    String expected = "mockedSuccess";
    when(valueService.getSuccessfulValue()).thenReturn(expected);

    // Execution
    String actual = underTest.getValue(true);

    // Verification
    assertEquals(expected, actual);
    verify(valueService, times(2)).tryService();
    verify(valueService).getSuccessfulValue();
    verifyNoMoreInteractions(valueService);
  }


}
