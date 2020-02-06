package io.lbert.hlist;

import com.google.auto.service.AutoService;
import io.lbert.hlist.processing.Field;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SupportedAnnotationTypes("io.lbert.hlist.annotations.Generic")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class GenericProcessor extends AbstractProcessor {

  private static final String SUFFIX = "Generic";

  private Types typeUtils;
  private Elements elementUtils;
  private Filer filer;
  private Messager messager;

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    typeUtils = processingEnv.getTypeUtils();
    elementUtils = processingEnv.getElementUtils();
    filer = processingEnv.getFiler();
    messager = processingEnv.getMessager();
  }

  @Override
  public boolean process(
      Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv
  ) {
    annotations.stream()
        .map(annotation -> {
          return roundEnv.getElementsAnnotatedWith(annotation).stream()
              .flatMap(el -> {
                if (el.getKind() != ElementKind.CLASS) {
                  error(el, "Only classes can be annotated with @%s",
                      annotation.getClass().getSimpleName());
                  return Stream.empty();
                } else {
                  writeFile((TypeElement) el);
                  return Stream.of("hi");
                }
              })
              .collect(Collectors.toList());
        })
        .collect(Collectors.toList());

    return true;
  }

  private void writeFile(TypeElement typeElement) {
    final String filename = getFilename(typeElement);
    final String packageName = getPackage(typeElement);
    final String className = getClassName(typeElement);
    log("Filename: " + filename);
    log("Package name: " + packageName);
    log("Class name: " + getClassName(typeElement));
    final var fields = getFields(typeElement);

    if(true) {
      try {
        JavaFileObject jfo = filer.createSourceFile(filename);
        PrintWriter out = new PrintWriter(jfo.openWriter());
        //Print package name
        out.print("package ");
        out.print(packageName);
        out.println(";");
        out.println();
        out.println("import static io.lbert.HList.*;");
        out.println();
        //Print class
        out.print("public class ");
        out.print(className);
        out.println(" {");
        out.println();
        //Body of class
        fields.stream().forEach(field -> {
          out.print("  public final ");
          out.print(field.className);
          out.print(" ");
          out.print(field.name);
          out.println(";");
        });
        out.println("");
        //create private constructor
        out.print("  private ");
        out.print(className);
        out.print("(");
        out.print(fields.stream().map(field ->
          String.format("%s %s", field.className, field.name)
        ).collect(Collectors.joining(", ")));
        out.println(") {");
        fields.stream().forEach(field -> {
          out.print("    this.");
          out.print(field.name);
          out.print(" = ");
          out.print(field.name);
          out.println(";");
        });
        out.println("  }");
        out.println();
        //create public of constructor
        out.print("  public static ");
        out.print(className);
        out.print(" of(");
        out.print(fields.stream().map(field ->
            String.format("%s %s", field.className, field.name)
        ).collect(Collectors.joining(", ")));
        out.println(") {");
        out.print("    return new ");
        out.print(className);
        out.print("(");
        out.print(fields.stream().map(f -> f.name).collect(Collectors.joining(", ")));
        out.println(");");
        out.println("  }");
        out.println();
        //create from
        out.print("  public static ");
        out.print(className);
        out.print(" from(");
        out.print(hlistType(fields));
        out.println(" hlist) {");
        out.print("    return of(");
        var a = IntStream.range(0, fields.size())
            .boxed()
            .map(i -> {
              var tails = IntStream.range(0, i).boxed()
                  .map(j -> "tail")
                  .collect(Collectors.joining("."));
              if(tails.length() > 0) {
                return String.format("hlist.%s.head", tails);
              } else {
                return String.format("hlist.head");
              }
            })
            .collect(Collectors.joining(", "));
        out.print(a);
        out.println(");");
        out.println("  }");
        out.println();
        //create to
        out.print("  public ");
        out.print(hlistType(fields));
        out.println(" to() {");
        out.print("    return ");
        fields.stream().forEach(field -> {
          out.print("cons(");
          out.print(field.name);
          out.print(", ");
        });
        out.print("nil()");
        out.print(fields.stream().map(i -> ")").collect(Collectors.joining()));
        out.println(";");
        out.println("  }");
        out.println();
        //create toString
//        out.println("  @Override");
//        out.println("  public String toString() {");
//        out.print("    return \"");
//        out.print(className);
//        out.print("(");
//
//        out.println("  }");
//        out.println();
        //End body class
        out.println("}");
        out.close();
      } catch (Exception e) {
        System.out.println("Error writing file: " + e.getMessage());
      }
    }
  }

  private static String hlistType(List<Field> fields) {
    return fields.stream()
        .map(f -> String.format("HCons<%s, ",f.className))
        .collect(Collectors.joining()) +
        "HNil" +
        fields.stream().map(i -> ">").collect(Collectors.joining());
  }

  private List<Field> getFields(TypeElement typeElement) {
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

  private String getFilename(TypeElement typeElement) {
    return getPackage(typeElement) + "." + getClassName(typeElement);
  }

  private String getPackage(TypeElement typeElement) {
    return elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
  }

  private String getClassName(TypeElement typeElement) {
    final String className = typeElement.getQualifiedName().toString();
    int lastDot = className.lastIndexOf('.');
    if (lastDot > 0) {
      return className.substring(lastDot + 1) + SUFFIX;
    }
    return "";
  }

  private void log(String s) {
    System.out.println(String.format("GenericProcessor.log: %s", s));
  }
  private void error(Element e, String msg, Object... args) {
    messager.printMessage(
        Diagnostic.Kind.ERROR,
        String.format(msg, args),
        e);
  }
}
