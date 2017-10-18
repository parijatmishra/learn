package net.nihilanth.demo.annot.processor;

import net.nihilanth.demo.annot.lib.Entity;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Main entry point for @Entity annotation processing.
 */
public class EntityProcessor extends AbstractProcessor {
    public static final String ERR_NOT_CLASS = "E0001: @Entity can only be put on a class";
    public static final String ERR_ABSTRACT = "E0002: @Entity cannot be put on an abstract class";
    public static final String ERR_NO_DFLT_CONSTUCTOR = "E0003: @Entity class does not have a public/default no-arg constructor";

    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Entity.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element e : roundEnv.getElementsAnnotatedWith(Entity.class)) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Processing", e);

            if (isNotConcreteClass(e)) {
                return true;
            }

            if (isMissingVisibleNoArgConstructor(e)) {
                return true;
            }

        }

        return true;
    }

    /*
     * Check that element has a no-arg constructor with public or default visibility
     * We check that either it has no explicit constructor, or if it does have
     * one or more explicit constructor, then one of them is a no-arg
     * constructor
     */
    private boolean isMissingVisibleNoArgConstructor(Element e) {
        boolean hasAccessibleNoArgConstructor = false;
        int numConstructors = 0;
        for (Element ee : e.getEnclosedElements()) {
            if (ee.getKind().equals(ElementKind.CONSTRUCTOR)) {
                numConstructors++;
                ExecutableElement exe = (ExecutableElement) ee;
                hasAccessibleNoArgConstructor = exe.getParameters().isEmpty() && (
                        exe.getModifiers().contains(Modifier.PUBLIC)
                                || exe.getModifiers().isEmpty()
                );
            }
        }

        if (numConstructors == 0)
            hasAccessibleNoArgConstructor = true;

        if (!hasAccessibleNoArgConstructor) {
            error(e, ERR_NO_DFLT_CONSTUCTOR);
            return true;
        }
        return false;
    }

    /*
     * Check that the annotated element is a concrete class, and not an interface or abstract class
     */
    private boolean isNotConcreteClass(Element e) {
        if (!e.getKind().equals(ElementKind.CLASS)) {
            error(e, ERR_NOT_CLASS);
            return true;
        }

        for (Modifier m : e.getModifiers()) {
            if (m.equals(Modifier.ABSTRACT)) {
                error(e, ERR_ABSTRACT);
                return true;
            }
        }
        return false;
    }

    private void error(Element e, String msg, Object... args) {
        this.messager.printMessage(
                Diagnostic.Kind.ERROR,
                String.format(msg, args),
                e
        );
    }

}
