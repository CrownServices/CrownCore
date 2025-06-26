package dev.crown.processor;

import com.google.auto.service.AutoService;
import dev.crown.annotation.CrownComponent;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("dev.crown.annotation.CrownComponent")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
public class CrownComponentValidator extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) {
            return false;
        }
        for (Element element : roundEnv.getElementsAnnotatedWith(CrownComponent.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
                        "@CrownComponent found on " + element.getSimpleName(), element);
            }
        }
        return true;
    }
}
