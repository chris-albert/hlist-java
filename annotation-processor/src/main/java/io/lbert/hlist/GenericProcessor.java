package io.lbert.hlist;

import com.google.auto.service.AutoService;
import io.lbert.hlist.processing.Field;
import io.lbert.hlist.processing.Generator;

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

    final Generator generator = Generator.of(typeElement, elementUtils);
    if(true) {
      try {
        JavaFileObject jfo = filer.createSourceFile(filename);
        PrintWriter out = new PrintWriter(jfo.openWriter());
        out.print(generator.generate());
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
