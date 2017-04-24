package com.github.zaplatynski.firstspirit.modules.fsm;

import org.needle4j.injection.InjectionProvider;
import org.needle4j.injection.InjectionTargetInformation;
import org.needle4j.mock.MockProvider;
import org.needle4j.mock.MockitoProvider;


public class MavenProjectInjectionProvider implements InjectionProvider<MockProvider> {

  private final MockProvider mockProvider;

  public MavenProjectInjectionProvider() {
    mockProvider = new MockitoProvider();
  }

  @Override
  public MockProvider getInjectedObject(Class<?> injectionPointType) {
    return mockProvider;
  }

  @Override
  public boolean verify(InjectionTargetInformation injectionTargetInformation) {
    final Class<?> type = injectionTargetInformation.getType();
    return type.isAssignableFrom(mockProvider.getClass());
  }

  @Override
  public Object getKey(InjectionTargetInformation injectionTargetInformation) {
    return MockProvider.class;
  }
}
