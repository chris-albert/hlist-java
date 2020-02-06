package io.lbert;

import io.lbert.hlist.annotations.Generic;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;

public class GenericAnnotationTest {

  @Generic
  public static class TestClass {
    String foo;
    Integer bar;
    Optional<LocalDate> date;
  }

  @Test
  public void checkOfConstructor() {
//    TestClassGeneric
//    System.out.println(io.lbert.TestClassGeneric.);
//    TestClass tc = TestClass.of("hi", 10);
  }

  @Test
  public void checkFieldImmutability() {

  }
}
