package net.nihilanth.demo.annot.processor;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static com.google.testing.compile.Compiler.javac;
import static com.google.testing.compile.CompilationSubject.*;

/**
 * Test functionality of EntityProcessor
 */
public class EntityProcessorTest {

    @Test
    public void abstractClassShouldError() {
        Compilation compilation = javac()
                .withProcessors(new EntityProcessor())
                .compile(JavaFileObjects.forResource("HelloEntityAbstract.java"));
        assertThat(compilation)
                .hadErrorContaining(EntityProcessor.ERR_ABSTRACT);
    }

    @Test
    public void interfaceShouldError() {
        Compilation compilation = javac()
                .withProcessors(new EntityProcessor())
                .compile(JavaFileObjects.forResource("HelloEntityInterface.java"));
        assertThat(compilation)
                .hadErrorContaining(EntityProcessor.ERR_NOT_CLASS);
    }

    @Test
    public void privateNoArgConstructorError() {
        Compilation compilation = javac()
                .withProcessors(new EntityProcessor())
                .compile(JavaFileObjects.forResource("HelloEntityWithPrivateDefaultConstructor.java"));
        assertThat(compilation)
                .hadErrorContaining(EntityProcessor.ERR_NO_DFLT_CONSTUCTOR);
    }

    @Test
    public void missingNoArgConstructorError() {
        Compilation compilation = javac()
                .withProcessors(new EntityProcessor())
                .compile(JavaFileObjects.forResource("HelloEntityWithNoDefaultConstructor.java"));
        assertThat(compilation)
                .hadErrorContaining(EntityProcessor.ERR_NO_DFLT_CONSTUCTOR);
    }

    @Test
    public void explicitNoArgConstructorOk() {
        Compilation compilation = javac()
                .withProcessors(new EntityProcessor())
                .compile(JavaFileObjects.forResource("HelloEntityWithExplicitNoArgConstructor.java"));
        assertThat(compilation)
                .succeededWithoutWarnings();
    }

    @Test
    public void emtptyClassBodyTest() {
        Compilation compilation = javac()
                .withProcessors(new EntityProcessor())
                .compile(JavaFileObjects.forResource("HelloEntity0.java"));
        assertThat(compilation).succeededWithoutWarnings();
        // assertThat(compilation).generatedSourceFile("Generated_HelloEntity0_Repository.java");
    }
}
