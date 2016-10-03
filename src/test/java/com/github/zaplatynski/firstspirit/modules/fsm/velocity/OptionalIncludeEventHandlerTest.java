package com.github.zaplatynski.firstspirit.modules.fsm.velocity;

import org.apache.velocity.runtime.RuntimeServices;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


public class OptionalIncludeEventHandlerTest {

  @Rule
  public MockitoRule injectMocks = MockitoJUnit.rule();

  @Mock
  private RuntimeServices runtimeServices;

  private OptionalIncludeEventHandler testling;

  @Before
  public void setUp() throws Exception {
    testling = new OptionalIncludeEventHandler();
    testling.setRuntimeServices(runtimeServices);


  }

  @Test
  public void includeEventSuccess() throws Exception {
    when(runtimeServices.getLoaderNameForResource(anyString())).thenReturn("non null Value");

    final String event = testling.includeEvent("../../../../myMod/target/module.xml",
        "src/main/fsm/module.vm", null);

    assertThat(event, is("src/main/fsm/../../../../myMod/target/module.xml"));
  }

  @Test
  public void includeEventFailed() throws Exception {
    when(runtimeServices.getLoaderNameForResource(anyString())).thenReturn(null);

    final String event = testling.includeEvent("../../../../myMod/target/module.xml",
        "src/main/fsm/module.vm", null);

    assertThat(event, is(nullValue()));
  }

}