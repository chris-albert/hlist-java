package io.lbert.hlist.processing;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Generator {

  private static final String SUFFIX = "Generic";

  private final String fileName;
  private final String packageName;
  private final String className;
  private final String interfaceName;
  private final List<Field> fields;

  private Generator(
      final String fileName,
      final String packageName,
      final String className,
      final String interfaceName,
      final List<Field> fields
  ) {
    this.fileName = fileName;
    this.packageName = packageName;
    this.className = className;
    this.interfaceName = interfaceName;
    this.fields = fields;
  }

  public static Generator of(
      final String fileName,
      final String packageName,
      final String className,
      final String interfaceName,
      final List<Field> fields
  ) {
    return new Generator(fileName, packageName, className, interfaceName, fields);
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
        .string("from(final")
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
                .string("final")
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

  private BetterBuilder generateGetters() {
    return BetterBuilder.nests(fields.stream()
        .map(this::generateGetter)
        .collect(Collectors.toList()));
  }

  private BetterBuilder generateGetter(Field field) {
    return BetterBuilder.empty()
        .string("@Override")
        .newline()
        .string("public")
        .string(field.className)
        .string(field.name)
        .spaces()
        .string("() {")
        .newline()
        .nest(
            BetterBuilder.empty()
            .string("return ")
            .string("this.")
            .string(field.name)
            .string(";")
        )
        .newline()
        .string("}")
        .newline();
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
        .string("implements")
        .string(interfaceName)
        .string("{")
        .spaces()
        .string("\n\n");

    return BetterBuilder.empty()
        .nest(packageBuilder)
        .nest(BetterBuilder.ofString("import static io.lbert.hlist.HList.*;\n\n"))
        .nest(classDef)
        .nest(generateFields().newline())
        .nest(generatePrivateConstructor().newline())
        .nest(generatePublicOf().newline())
        .nest(generateFrom().newline())
        .nest(generateTo())
        .nest(generateGetters())
        .string("}\n")
        .build();
  }


  public String getFilename() {
    return this.fileName;
  }

  public static Generator of(
      final TypeElement typeElement,
      final Elements elementUtils
  ) {
    return of(
        getFilename(typeElement, elementUtils),
        getPackage(typeElement, elementUtils),
        getClassName(typeElement),
        getInterfaceName(typeElement),
        getFields(typeElement)
    );
  }

  private static List<Field> getFields(TypeElement typeElement) {
    return getFieldsInterface(typeElement);
  }

  private static List<Field> getFieldsInterface(TypeElement typeElement) {
    return typeElement.getEnclosedElements().stream()
        .flatMap(el -> {
          if(el.getKind() == ElementKind.METHOD) {
            final ExecutableElement ee = (ExecutableElement) el;
            if(ee.asType().getKind() == TypeKind.EXECUTABLE) {
              return Stream.of(
                  Field.of(
                      ee.getSimpleName().toString(),
                      ee.getReturnType().toString()
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

  private static String getInterfaceName(TypeElement typeElement) {
    return typeElement.getQualifiedName().toString();
  }

  private static void log(String s) {
    if(true) {
      System.out.println(String.format("GenericProcessor.log: %s", s));
    }
  }
}
