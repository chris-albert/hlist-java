package io.lbert;

import org.junit.Test;

import static io.lbert.HList.*;
import static org.junit.Assert.*;

public class HListTest {

  @Test
  public void asdf() {
    HCons<String, HCons<Integer, HNil>> hlist = cons("hi", cons(10, nil()));
    assertEquals(hlist.head, "hi");
    assertEquals(hlist.tail.head, (Integer) 10);
  }

  public static class TestClass {

    public final String foo;
    public final Integer bar;

    private TestClass(final String foo, final Integer bar) {
      this.foo = foo;
      this.bar = bar;
    }

    public static TestClass of(final String foo, final Integer bar) {
      return new TestClass(foo, bar);
    }

    public static TestClass from(HCons<String, HCons<Integer, HNil>> hlist) {
      return of(hlist.head, hlist.tail.head);
    }

    public HCons<String, HCons<Integer, HNil>> to() {
      return cons(foo, cons(bar, nil()));
    }
  }

  @Test
  public void idk() {
    HCons<String, HCons<Integer, HNil>> stringIntegerHList = cons("hi", cons(10, nil()));
    final TestClass tc = TestClass.from(stringIntegerHList);
    assertEquals(tc.foo, "hi");
    assertEquals(tc.bar, (Integer) 10);
    HCons<String, HCons<Integer, HNil>> otherHList = tc.to();
    assertEquals(otherHList.head, "hi");
    assertEquals(otherHList.tail.head, (Integer) 10);
  }
}