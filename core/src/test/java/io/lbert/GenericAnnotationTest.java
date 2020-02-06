package io.lbert;

import io.lbert.hlist.annotations.Generic;
import org.junit.Test;

public class GenericAnnotationTest {

  @Generic
  public static class TestClass {
    String foo;
    Integer bar;
  }

  @Test
  public void checkOfConstructor() {
//    TestClass tc = TestClass.of("hi", 10);
  }

  @Test
  public void checkFieldImmutability() {

  }
}
