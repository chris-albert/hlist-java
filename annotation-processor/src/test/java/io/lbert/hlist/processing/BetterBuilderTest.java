package io.lbert.hlist.processing;

import org.junit.Test;

import static org.junit.Assert.*;

public class BetterBuilderTest {

  @Test
  public void zipShouldZip() {
    final var l = BetterBuilder.empty()
        .string("foo");
    final var r = BetterBuilder.empty()
        .string("bar");

    assertEquals(
        "foobar",
        BetterBuilder.zip(l, r).build()
    );
  }

  @Test
  public void interleaveShouldWork() {
    final var bb = BetterBuilder.empty()
        .string("foo")
        .string("bar")
        .interleave(" ");
    assertEquals(
        "foo bar",
        bb.build()
    );
  }

  @Test
  public void mapShouldWork() {
    final var bb = BetterBuilder.empty()
        .string("foo")
        .string("bar")
        .map(s -> String.format("-%s-", s));
    assertEquals(
        "-foo--bar-",
        bb.build()
    );
  }

  @Test
  public void surround() {
    final var bb = BetterBuilder.empty()
        .string("foo")
        .string("bar")
        .surround("(", ")");
    assertEquals(
        "(foobar)",
        bb.build()
    );
  }
}