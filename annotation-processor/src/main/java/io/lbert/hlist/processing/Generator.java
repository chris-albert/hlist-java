package io.lbert.hlist.processing;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Generator {

  private static final String SUFFIX = "Generic";

  private final String fileName;
  private final String packageName;
  private final String className;
  private final List<Field> fields;

  private Generator(String fileName, String packageName, String className, List<Field> fields) {
    this.fileName = fileName;
    this.packageName = packageName;
    this.className = className;
    this.fields = fields;
  }

  public static Generator of(
      String fileName, String packageName, String className, List<Field> fields
  ) {
    return new Generator(fileName, packageName, className, fields);
  }

  public BetterBuilder generateFields() {
    return BetterBuilder.nests(fields.stream()
        .map(field ->
            BetterBuilder.empty()
            .string("private")
            .string("final")
            .string(field.className)
            .string(field.name)
            .spaces()
            .string(";")
        ).collect(Collectors.toList()))
        .interleave("\n")
        .string("\n");
  }

  public BetterBuilder generatePrivateConstructor() {
    final var def = BetterBuilder.empty()
        .string("private")
        .string(className)
        .nest(fieldsAsParameters())
        .string("{")
        .spaces()
        .string("\n");
    final var body = BetterBuilder.nests(fields.stream()
        .map(field ->
            BetterBuilder.empty()
            .string("this.")
            .string(field.name)
            .string(" = ")
            .string(field.name)
            .string(";")
        ).collect(Collectors.toList()))
        .interleave("\n");
    return BetterBuilder.zip(def, body).string("\n}\n");
  }

  public BetterBuilder generatePublicOf() {
    final var def = BetterBuilder.empty()
        .string("public")
        .string("static")
        .string(className)
        .string("of")
        .nest(fieldsAsParameters())
        .string("{\n")
        .spaces();
    final var body = BetterBuilder.empty()
        .string("return")
        .string("new")
        .string(className)
        .nest(
            BetterBuilder.nests(fields.stream()
            .map(f -> BetterBuilder.empty().string(f.name))
            .collect(Collectors.toList()))
            .commas().parenthethis()
        )
        .spaces()
        .string(";");
    return BetterBuilder.zip(def, body).string("\n}\n");
  }

  public BetterBuilder generateFrom() {
    final var def = BetterBuilder.empty()
        .string("public")
        .string("static")
        .string(className)
        .string("from(")
        .nest(
            generateHListType()
            .string("hlist")
            .spaces()
        )
        .string(") {\n")
        .spaces();
    final var body = BetterBuilder.empty()
        .string("return")
        .string("of(")
        .spaces()
        .nest(
            BetterBuilder.nests(
                IntStream.range(0, fields.size())
                .boxed()
                .map(i -> {
                  final var tails = BetterBuilder
                      .repeat(".tail()", i);
                  return BetterBuilder.empty()
                      .string("hlist")
                      .nest(tails)
                      .string(".head()");
                })
                .collect(Collectors.toList())
            )
            .commas()
        )
        .string(");");

    return BetterBuilder.zip(def, body).string("\n}\n");
  }

  public BetterBuilder generateTo() {
    final var def = BetterBuilder.empty()
        .string("public")
        .nest(generateHListType())
        .string("to() {\n")
        .spaces();
    final var body = BetterBuilder.empty()
        .string("return ")
        .nest(
            BetterBuilder.nests(
                fields.stream()
                .map(field -> BetterBuilder.empty()
                .string("cons(").string(field.name).string(", "))
                .collect(Collectors.toList())
            )
            .string("nil()")
            .nest(BetterBuilder.repeat(")", fields.size()))
        )
        .string(";\n");
    return BetterBuilder.zip(def, body).string("}\n");
  }

  public BetterBuilder fieldsAsParameters() {
    return BetterBuilder.nests(
        fields.stream().map(field ->
            BetterBuilder.empty()
            .string(field.className)
            .string(field.name)
            .spaces()
        ).collect(Collectors.toList())
    )
        .interleave(", ")
        .parenthethis();
  }

  private BetterBuilder generateHListType() {
    final var end = BetterBuilder
        .repeat(">", fields.size());
    return BetterBuilder.ofBuilder(BetterBuilder.nests(
        fields.stream()
        .map(field ->
            BetterBuilder.empty()
            .string("HCons<")
            .string(field.className)
        )
        .collect(Collectors.toList())
    ).commas()
        .string(", HNil")
        .nest(end));
  }

  public String generate() {
    final var packageBuilder = BetterBuilder.empty()
        .string("package ")
        .string(packageName)
        .string(";\n\n");

    final var classDef = BetterBuilder.empty()
        .string("public")
        .string("class")
        .string(className)
        .string("{")
        .spaces()
        .string("\n\n");

    return BetterBuilder.empty()
        .nest(packageBuilder)
        .nest(BetterBuilder.ofString("import static io.lbert.HList.*;\n\n"))
        .nest(classDef)
        .nest(generateFields())
        .nest(generatePrivateConstructor())
        .nest(generatePublicOf())
        .nest(generateFrom())
        .nest(generateTo())
        .string("}\n")
        .build();
  }

  public String getFilename() {
    return this.fileName;
  }

  public static Generator of(
      TypeElement typeElement,
      Elements elementUtils
  ) {
    return of(
        getFilename(typeElement, elementUtils),
        getPackage(typeElement, elementUtils),
        getClassName(typeElement),
        getFields(typeElement)
    );
  }

  private static List<Field> getFields(TypeElement typeElement) {
    return typeElement.getEnclosedElements().stream()
        .flatMap(el -> {
          if(el.getKind() == ElementKind.FIELD) {
            final VariableElement ve = (VariableElement) el;
            if(ve.asType().getKind() == TypeKind.DECLARED) {
              DeclaredType declaredFieldType = (DeclaredType) ve.asType();
              return Stream.of(
                  Field.of(
                      ve.getSimpleName().toString(),
                      declaredFieldType.toString()
                  )
              );
            }
          }
          return Stream.empty();
        })
        .collect(Collectors.toList());
  }

  private static String getFilename(TypeElement typeElement, Elements elementUtils) {
    return getPackage(typeElement, elementUtils) + "." + getClassName(typeElement);
  }

  private static String getPackage(TypeElement typeElement, Elements elementUtils) {
    return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
  }

  private static String getClassName(TypeElement typeElement) {
    final String className = typeElement.getQualifiedName().toString();
    int lastDot = className.lastIndexOf('.');
    if (lastDot > 0) {
      return className.substring(lastDot + 1) + SUFFIX;
    }
    return "";
  }
}
