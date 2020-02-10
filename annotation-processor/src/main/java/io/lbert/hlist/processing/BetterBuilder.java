package io.lbert.hlist.processing;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class BetterBuilder {

  private final List<Element> elements;

  private BetterBuilder(final List<Element> elements) {
    this.elements = elements;
  }

  public static BetterBuilder of(final List<Element> elements) {
    return new BetterBuilder(elements);
  }

  public static BetterBuilder ofString(final String str) {
    return of(List.of(StringElement.of(str)));
  }

  public static BetterBuilder ofBuilder(final BetterBuilder bb) {
    return of(List.of(BuilderElement.of(bb)));
  }

  public static BetterBuilder empty() {
    return of(List.of());
  }

  public static BetterBuilder zip(
      final BetterBuilder left,
      final BetterBuilder right
  ) {
    final var l = left.elements.toArray(new Element[0]);
    final var r = right.elements.toArray(new Element[0]);
    final var t = Stream.of(l, r).flatMap(Stream::of)
        .toArray(Element[]::new);

    return of(Arrays.asList(t));
  }

  public BetterBuilder map(Function<String, String> strFunc) {
    return of(elements.stream()
        .flatMap(el -> {
          if(el instanceof StringElement) {
            return Stream.of(StringElement.of(strFunc.apply(((StringElement) el).str)));
          } else {
            return Stream.of(el);
          }
        }).collect(Collectors.toList()));
  }

  public BetterBuilder commas() {
    return interleave(", ");
  }

  public BetterBuilder spaces() {
    return interleave(" ");
  }

  public BetterBuilder surround(final String left, final String right) {
    return zip(zip(
        ofString(left),
        this
    ),ofString(right));
  }

  public BetterBuilder parenthethis() {
    return surround("(", ")");
  }

  public BetterBuilder string(final String str) {
    return zip(this, ofString(str));
  }

  public static BetterBuilder repeat(final String str, final Integer count) {
    return nests(IntStream.range(0, count)
        .boxed()
        .map(i -> ofString(str))
        .collect(Collectors.toList()));
  }

  public BetterBuilder nest(final BetterBuilder bb) {
    return zip(this, ofBuilder(bb));
  }

  public static BetterBuilder nests(final List<BetterBuilder> builders) {
    return of(
        builders.stream()
        .map(BuilderElement::of)
        .collect(Collectors.toList())
    );
  }

  public BetterBuilder interleave(final String str) {
    final var newEls = elements.stream()
        .flatMap(el ->
          Stream.of(el, StringElement.of(str))
        )
        .collect(Collectors.toList());
    return of(newEls.subList(0, newEls.size() - 1));
  }

  public String build() {
    return elements.stream()
        .flatMap(el -> {
          if(el instanceof StringElement) {
            return Stream.of(((StringElement) el).str);
          } else if(el instanceof BuilderElement) {
            return Stream.of(((BuilderElement) el).builder.build());
          }
          return Stream.empty();
        })
        .collect(Collectors.joining());
  }

  interface Element {}

  private static class StringElement implements Element {

    private final String str;

    private StringElement(final String str) {
      this.str = str;
    }

    public static StringElement of(final String str) {
      return new StringElement(str);
    }
  }

  private static class BuilderElement implements Element {

    private final BetterBuilder builder;

    private BuilderElement(final BetterBuilder builder) {
      this.builder = builder;
    }

    public static BuilderElement of(final BetterBuilder builder) {
      return new BuilderElement(builder);
    }
  }
}
