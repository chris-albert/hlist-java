package io.lbert.hlist;

import java.util.function.Function;

public interface HList<A extends HList<A>> {

  Boolean isNil();

  default Boolean isCons() {
    return !isNil();
  }

  static <H, T extends HList<T>> HCons<H, T> cons(H h, T t) {
    return HCons.of(h, t);
  }

  static HNil nil() {
    return HNil.of();
  }

  final class HCons<H, T extends HList<T>> implements HList<HCons<H, T>> {

    private final H head;
    private final T tail;

    private HCons(final H head, final T tail) {
      this.head = head;
      this.tail = tail;
    }

    public static <H, T extends HList<T>> HCons<H, T> of(
        final H head,
        final T tail
    ) {
      return new HCons<>(head, tail);
    }

    public H head() {
      return this.head;
    }

    public T tail() {
      return this.tail;
    }

    public <B> HCons<B, T> map(Function<H, B> func) {
      return of(
          func.apply(head),
          tail
      );
    }

    @Override
    public Boolean isNil() {
      return false;
    }
  }

  final class HNil implements HList<HNil> {

    private static final HNil H_NIL = new HNil();

    public static HNil of() {
      return H_NIL;
    }

    @Override
    public Boolean isNil() {
      return true;
    }
  }

}
