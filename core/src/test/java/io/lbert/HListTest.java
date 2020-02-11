package io.lbert;

import org.junit.Test;

import static io.lbert.hlist.HList.*;
import static org.junit.Assert.*;

public class HListTest {

  @Test
  public void simpleTest() {
    HCons<String, HCons<Integer, HNil>> hlist = cons("hi", cons(10, nil()));
    assertEquals(hlist.head(), "hi");
    assertEquals(hlist.tail().head(), (Integer) 10);
  }

  @Test
  public void mapTest() {
    HCons<String, HCons<Integer, HNil>> hlist = cons("hi", cons(10, nil()));
    HCons<Integer, HCons<Integer, HNil>> mapped = hlist.map(String::length);
    assertEquals(mapped.head(), (Integer) 2);
    assertEquals(mapped.tail().head(), (Integer) 10);
  }

}