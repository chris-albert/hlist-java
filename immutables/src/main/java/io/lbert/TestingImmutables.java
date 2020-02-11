package io.lbert;

import org.immutables.value.Value;

public class TestingImmutables {

  @Value.Immutable
  interface TestImmutables {
    String foo();
    Integer bar();
  }
}
