package io.lbert;

import io.lbert.hlist.annotations.Generic;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Optional;
import static org.junit.Assert.*;
import static io.lbert.hlist.HList.*;

public class GenericAnnotationTest {

  private static final LocalDate date = LocalDate.of(2020,1,1);

  @Generic
  interface TestInterface {
    String foo();
    Integer bar();
    Optional<LocalDate> date();
  }

  @Test
  public void checkOfConstructor() {
    final var ti = TestInterfaceGeneric.of("foo", 10, Optional.of(date));
    assertEquals("foo", ti.foo());
    assertEquals((Integer) 10 , ti.bar());
    assertEquals(Optional.of(date), ti.date());
  }

  @Test
  public void checkFromConstructor() {
    final var hlist = cons("foo", cons(10, cons(Optional.of(date), nil())));
    final var ti = TestInterfaceGeneric.from(hlist);
    assertEquals("foo", ti.foo());
    assertEquals((Integer) 10 , ti.bar());
    assertEquals(Optional.of(date), ti.date());
  }

  @Test
  public void checkTo() {
    final var ti = TestInterfaceGeneric.of("foo", 10, Optional.of(date));
    final var hlist = ti.to();
    assertEquals("foo", hlist.head());
    assertEquals((Integer) 10 , hlist.tail().head());
    assertEquals(Optional.of(date), hlist.tail().tail().head());
  }
}
