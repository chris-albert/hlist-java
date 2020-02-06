package io.lbert.hlist;

import com.google.auto.service.AutoService;
import io.lbert.hlist.annotations.Generic;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Set;
import java.util.stream.Collectors;
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
    log("Filename: " + filename);
    log("Package name: " + packageName);
    log("Class name: " + getClassName(typeElement));

    if(true) {
      try {
        JavaFileObject jfo = filer.createSourceFile(filename);
        PrintWriter out = new PrintWriter(jfo.openWriter());
        //Print package name
        out.print("package ");
        out.print(packageName);
        out.println(";");
        out.println();
        //Print class
        out.print("public class ");
        out.print(getClassName(typeElement));
        out.println(" {");
        //Body of class
        out.println("  public static String idk() {");
        out.println("    return \"idk\";");
        out.println("  }");
        //End body class
        out.println("}");
        out.close();
      } catch (Exception e) {
        System.out.println("Error writing file: " + e.getMessage());
      }

    }
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
