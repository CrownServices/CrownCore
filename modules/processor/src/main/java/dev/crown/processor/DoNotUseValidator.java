package dev.crown.processor;

import com.google.auto.service.AutoService;
import dev.crown.annotation.DoNotUse;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.crown.annotation.DoNotUse")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class DoNotUseValidator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(DoNotUse.class)) {
            DoNotUse annotation = element.getAnnotation(DoNotUse.class);
            String reason = annotation != null && !annotation.reason().isEmpty() ? " Reason: " + annotation.reason() : "";
            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
                    "@DoNotUse is present on " + element.getSimpleName() + "." + reason, element);
        }
        return true;
    }
}
