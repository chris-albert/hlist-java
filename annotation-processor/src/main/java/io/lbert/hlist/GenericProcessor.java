package io.lbert.hlist;

import com.google.auto.service.AutoService;
import io.lbert.hlist.processing.Generator;

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
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SupportedAnnotationTypes("io.lbert.hlist.annotations.Generic")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class GenericProcessor extends AbstractProcessor {


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
                if (el.getKind() != ElementKind.INTERFACE) {
                  error(el, "Only interfaces can be annotated with @%s",
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

    final Generator generator = Generator.of(typeElement, elementUtils);
    try {
      JavaFileObject jfo = filer.createSourceFile(generator.getFilename());
      PrintWriter out = new PrintWriter(jfo.openWriter());
      out.print(generator.generate());
      out.close();
    } catch (Exception e) {
      System.out.println("Error writing file: " + e.getMessage());
    }
  }

  private void error(Element e, String msg, Object... args) {
    messager.printMessage(
        Diagnostic.Kind.ERROR,
        String.format(msg, args),
        e);
  }
}
