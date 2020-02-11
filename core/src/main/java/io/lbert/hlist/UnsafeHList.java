package io.lbert.hlist;

import java.util.ArrayList;
import java.util.List;

import static io.lbert.hlist.HList.*;

public class UnsafeHList {

  public static List<Object> toList(HList<?> hlist) {
    final List<Object> objs = new ArrayList<>();
    var currHList = hlist;
    while(currHList.isCons()) {
      final HCons<?, ?> cons = (HCons<?, ?>) currHList;
      objs.add(cons.head());
      currHList = cons.tail();
    }
    return objs;
  }

}
