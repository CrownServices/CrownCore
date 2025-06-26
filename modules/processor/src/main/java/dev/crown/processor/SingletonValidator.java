package dev.crown.processor;

import com.google.auto.service.AutoService;
import dev.crown.annotation.Singleton;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.crown.annotation.Singleton")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class SingletonValidator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(Singleton.class)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "@Singleton is present on " + element.getSimpleName(), element);
        }
        return true;
    }
}
