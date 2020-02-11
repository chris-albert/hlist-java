package io.lbert;

import org.junit.Test;

import static io.lbert.HList.*;
import static org.junit.Assert.*;

public class HListTest {

  @Test
  public void simpleTest() {
    HCons<String, HCons<Integer, HNil>> hlist = cons("hi", cons(10, nil()));
    assertEquals(hlist.head(), "hi");
    assertEquals(hlist.tail().head(), (Integer) 10);
  }

}