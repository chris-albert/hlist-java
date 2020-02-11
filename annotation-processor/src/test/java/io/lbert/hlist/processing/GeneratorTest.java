package io.lbert.hlist.processing;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class GeneratorTest {

  private static final Generator generator =
      Generator.of(
          "io.lbert.TestGeneric",
          "io.lbert",
          "Test",
          "TestGeneric",
          List.of(
              Field.of("foo", "String"),
              Field.of("bar", "Integer"),
              Field.of("baz", "Option<Boolean>")
          )
      );

  @Test
  public void generateFields() {
    assertEquals(
        "private final String foo;\n" +
        "private final Integer bar;\n" +
        "private final Option<Boolean> baz;\n",
        generator.generateFields().build()
    );
  }
}