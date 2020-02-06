package io.lbert.hlist;

import com.google.auto.service.AutoService;
import io.lbert.hlist.annotations.Generic;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("io.lbert.hlist.annotations.Generic")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class GenericProcessor extends AbstractProcessor {

  @Override
  public boolean process(
      Set<? extends TypeElement> annotations,
      RoundEnvironment roundEnv
  ) {
    roundEnv.getElementsAnnotatedWith(Generic.class).stream()
        .map(el -> {
          System.out.println(" --FROM-PROCESSOR-- " + ((Element) el).getSimpleName());

          return "hi";
        })
        .collect(Collectors.toList());
    return false;
  }
}
