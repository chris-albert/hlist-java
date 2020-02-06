package io.lbert.hlist.processing;

public class Field {

  public final String name;
  public final String className;

  private Field(final String name, final String className) {
    this.name = name;
    this.className = className;
  }

  public static Field of(final String name, final String className) {
    return new Field(name, className);
  }

  @Override
  public String toString() {
    return String.format(
      "Fields(%s, %s)",
      name, className
    );
  }
}
