package org.yuyan.room.annotation.processor.database;

import com.squareup.javapoet.*;
import org.yuyan.room.annotation.processor.helper.AnnotationProcessorHelper;
import org.yuyan.room.dao.Dao;
import org.yuyan.room.database.DaoMethod;
import org.yuyan.room.database.Database;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.util.*;

import static java.lang.System.out;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DatabaseAnnotationProcessor extends AbstractProcessor implements DatabaseProcessable{
    private final List<Class<?>> supportClassList = new ArrayList<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        supportClassList.add(Database.class);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportClassNames = new HashSet<>();
        for (Class<?> aClass : supportClassList) {
            supportClassNames.add(aClass.getCanonicalName());
        }
        return supportClassNames;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!AnnotationProcessorHelper.processable(annotations, roundEnv)) {
            return false;
        }
        roundEnv.getElementsAnnotatedWith(Database.class).forEach(elementAnnotatedDatabase -> {
            String pkgName = elementAnnotatedDatabase.getEnclosingElement().toString();
            String clsName = elementAnnotatedDatabase.getSimpleName().toString();

            TypeSpec.Builder containerClassBuilder = createContainerClassBuilder(pkgName, clsName);

            Map<FieldSpec, MethodSpec> daoReference = createDaoReference(elementAnnotatedDatabase);
            daoReference.forEach((fieldSpec, methodSpec) -> {
                containerClassBuilder.addField(fieldSpec);
                containerClassBuilder.addMethod(methodSpec);
            });


            JavaFile.Builder fileBuilder = JavaFile.builder(pkgName, containerClassBuilder.build());
            JavaFile javaFile = fileBuilder.build();
            try {
                // javaFile.writeTo(System.out);
                javaFile.writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return false;
    }

    @Override
    public TypeSpec.Builder createContainerClassBuilder(String pkgName, String clsName) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(
                clsName + AnnotationProcessorHelper.DB_IMPL_SUFFIX);
        classBuilder.addModifiers(Modifier.PUBLIC);
        classBuilder.superclass(ClassName.get(pkgName, clsName));
        return classBuilder;
    }

    @Override
    public Map<FieldSpec, MethodSpec> createDaoReference(Element elementAnnotatedDatabase) {
        Map<FieldSpec, MethodSpec> daoMethodWithSingletonField = new HashMap<>();
        List<ExecutableElement> executableElementList = ElementFilter
                .methodsIn(elementAnnotatedDatabase.getEnclosedElements());
        for (ExecutableElement methodElement : executableElementList) {
            DaoMethod daoMethod = methodElement.getAnnotation(DaoMethod.class);
            if (daoMethod == null) {
                continue;
            }
            MethodSpec.Builder daoMethodBuilder = AnnotationProcessorHelper.formMethodBuilder(methodElement, true);
            TypeName daoType = TypeName.get(methodElement.getReturnType());
            String singletonName = methodElement.getSimpleName() + AnnotationProcessorHelper.FIELD_INSTANCE_SUFFIX;
            FieldSpec daoInstanceField = FieldSpec.builder(daoType, singletonName).build();
            daoMethodBuilder.addCode("if ($N != null) {\n", singletonName);
            daoMethodBuilder.addCode("    return $N;\n", singletonName);
            daoMethodBuilder.addCode("} else {\n");
            daoMethodBuilder.addCode("    synchronized(this) {\n");
            daoMethodBuilder.addCode("        if ($N == null) {\n", singletonName);
            daoMethodBuilder.addCode("            $N = new $T$L(configure);\n", singletonName, daoType, AnnotationProcessorHelper.DB_IMPL_SUFFIX);
            daoMethodBuilder.addCode("        }\n");
            daoMethodBuilder.addCode("        return $N;\n", singletonName);
            daoMethodBuilder.addCode("    }\n");
            daoMethodBuilder.addCode("}\n");
            daoMethodWithSingletonField.put(daoInstanceField, daoMethodBuilder.build());
        }
        return daoMethodWithSingletonField;
    }
}