package io.lbert;

public class HList<A extends HList<A>> {

  public static <H, T extends HList<T>> HCons<H, T> cons(H h, T t) {
    return new HCons<>(h, t);
  }

  public static HNil nil() {
    return HNil.of();
  }

  public static final class HCons<H, T extends HList<T>> extends HList<HCons<H, T>> {
    private final H head;
    private final T tail;

    private HCons(final H head, final T tail) {
      this.head = head;
      this.tail = tail;
    }

    public H head() {
      return this.head;
    }

    public T tail() {
      return this.tail;
    }
  }

  public static final class HNil extends HList<HNil> {

    private static final HNil H_NIL = new HNil();

    public static HNil of() {
      return H_NIL;
    }
  }

}
