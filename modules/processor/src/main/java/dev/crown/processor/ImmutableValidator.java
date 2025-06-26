package dev.crown.processor;

import com.google.auto.service.AutoService;
import dev.crown.annotation.Immutable;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.crown.annotation.Immutable")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class ImmutableValidator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(Immutable.class)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                    "@Immutable is present on " + element.getSimpleName(), element);
        }
        return true;
    }
}
