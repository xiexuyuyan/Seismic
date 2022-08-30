package org.yuyan.room.annotation.processor.dao;

import com.squareup.javapoet.*;
import org.yuyan.room.annotation.processor.helper.AnnotationProcessorHelper;
import org.yuyan.room.base.DatabaseConfigure;
import org.yuyan.room.dao.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class DaoAnnotationProcessor extends AbstractProcessor implements DaoProcessable {
    private final List<Class<?>> supportClassList = new ArrayList<>();
    private final static List<Class<? extends Annotation>> curdList = new ArrayList<>();


    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        supportClassList.add(Dao.class);

        curdList.add(Query.class);
        curdList.add(Delete.class);
        curdList.add(Insert.class);
        curdList.add(Update.class);
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
        roundEnv.getElementsAnnotatedWith(Dao.class).forEach(elementAnnotatedDatabase -> {
            String pkgName = elementAnnotatedDatabase.getEnclosingElement().toString();
            String clsName = elementAnnotatedDatabase.getSimpleName().toString();

            TypeSpec.Builder containerClassBuilder = createContainerClassBuilder(pkgName, clsName);

            containerClassBuilder.addField(DatabaseConfigure.class, "configure");
            MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(ParameterSpec.builder(DatabaseConfigure.class, "configure").build());
            constructorBuilder.addStatement("this.configure = configure");
            containerClassBuilder.addMethod(constructorBuilder.build());

            Map<ExecutableElement, Class<? extends Annotation>> curdElementMap = parseElement(elementAnnotatedDatabase);
            curdElementMap.forEach((element, aClass) -> {
                System.out.println("dao handle: " + element.getSimpleName() + "=" + aClass.getName());
                MethodSpec.Builder methodBuilder = CurdMethod.baseBuilder(element);

                if (CurdMethod.isCurdMethod(aClass.getCanonicalName())) {
                    CurdMethod.setBasicCurdMethod(methodBuilder, element, aClass);
                }

                containerClassBuilder.addMethod(methodBuilder.build());
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

    public static Map<ExecutableElement, Class<? extends Annotation>> parseElement(Element elementAnnotatedDao) {
        Map<ExecutableElement, Class<? extends Annotation>> curdElementMap = new HashMap<>();
        ElementFilter.methodsIn(elementAnnotatedDao.getEnclosedElements()).forEach(executableElement -> {
            Class<? extends Annotation> cls = isAnnotatedCurd(executableElement);
            if (cls != null) {
                curdElementMap.put(executableElement, cls);
            }
        });
        return curdElementMap;
    }

    private static Class<? extends Annotation> isAnnotatedCurd(ExecutableElement element){
        for (Class<? extends Annotation> aClass : curdList) {
            if (element.getAnnotation(aClass) != null) {
                return aClass;
            }
        }
        return null;
    }


    @Override
    public TypeSpec.Builder createContainerClassBuilder(String pkgName, String clsName) {
        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(
                clsName + AnnotationProcessorHelper.DB_IMPL_SUFFIX);
        classBuilder.addModifiers(Modifier.PUBLIC);
        classBuilder.addSuperinterface(ClassName.get(pkgName, clsName));
        return classBuilder;
    }
}