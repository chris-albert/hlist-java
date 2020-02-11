package io.lbert.hlist;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static io.lbert.hlist.HList.*;

public class UnsafeHListTest {

  @Test
  public void toList() {
    final HCons<String, HCons<Integer, HNil>> hlist = cons("foo", cons(10, nil()));
    final List<Object> expected = List.of("foo", 10);
    assertEquals(
        expected,
        UnsafeHList.toList(hlist)
    );
  }
}