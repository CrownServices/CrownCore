package dev.crown.processor;

import com.google.auto.service.AutoService;
import dev.crown.annotation.Beta;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.crown.annotation.Beta")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class BetaValidator extends AbstractProcessor {

    /**
     * {@inheritDoc}
     *
     * @param annotations
     * @param roundEnv
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false; // No annotations to process
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(Beta.class)) {
            if (element.getKind() == ElementKind.CLASS || element.getKind() == ElementKind.METHOD || element.getKind() == ElementKind.FIELD) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                        "The @Beta annotation is used on " + element.getSimpleName() + ". This feature is in beta and may change in future releases.",
                        element);
            }
        }
        return true; // Indicate that the annotations have been processed
    }
}
