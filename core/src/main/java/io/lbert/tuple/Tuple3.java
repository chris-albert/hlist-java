package io.lbert.tuple;

public class Tuple3<T1, T2, T3> {

  public final T1 _1;
  public final T2 _2;
  public final T3 _3;

  private Tuple3(final T1 t1, final T2 t2, final T3 t3) {
    this._1 = t1;
    this._2 = t2;
    this._3 = t3;
  }

  public static <T1, T2, T3> Tuple3<T1, T2, T3> of(final T1 t1, final T2 t2, final T3 t3) {
    return new Tuple3<>(t1, t2, t3);
  }
}
