package dev.crown.processor;

import com.google.auto.service.AutoService;
import dev.crown.annotation.ThreadSafe;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.crown.annotation.ThreadSafe")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ThreadSafeValidator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(ThreadSafe.class)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "@ThreadSafe is present on " + element.getSimpleName(), element);
        }
        return true;
    }
}
