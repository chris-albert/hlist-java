package io.lbert;

import io.lbert.hlist.HList;
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

  @Test
  public void zipTest() {
    HCons<String, HCons<Integer, HNil>> left = cons("hi", cons(10, nil()));
    HCons<Boolean, HCons<Double, HNil>> right = cons(true, cons(1.1, nil()));
//    HCons<String, HCons<Integer, HCons<Boolean, HCons<Double, HNil>>>> zipped =
//        HList.zip(left, right);
  }

}