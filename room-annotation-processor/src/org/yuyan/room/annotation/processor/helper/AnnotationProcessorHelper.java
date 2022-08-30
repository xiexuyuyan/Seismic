package org.yuyan.room.annotation.processor.helper;

import com.squareup.javapoet.*;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Set;

public class AnnotationProcessorHelper {
    public static final String DB_IMPL_SUFFIX = "_Impl";
    public static final String FIELD_INSTANCE_SUFFIX = "_Instance";

    public static boolean processable(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv == null || roundEnv.processingOver()) {
            return false;
        }
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        return true;
    }



    public static MethodSpec.Builder formMethodBuilder(ExecutableElement element, boolean isOverride) {
        String methodName = element.getSimpleName().toString();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName);

        if (isOverride) {
            ClassName override = ClassName.get("java.lang", "Override");
            methodBuilder.addAnnotation(override);
        }

        element.getModifiers().forEach(modifier -> {
            switch (modifier) {
                case PUBLIC:
                case PROTECTED:
                    methodBuilder.addModifiers(modifier);
                case PRIVATE:
                case DEFAULT:
                case STATIC:
                case FINAL:
                case ABSTRACT:
                    break;
            }
        });

        element.getParameters().forEach(variableElement -> {
            String paramName = variableElement.getSimpleName().toString();
            TypeName clsType = TypeName.get(variableElement.asType());
            ParameterSpec.Builder paramBuilder = ParameterSpec.builder(clsType, paramName);
            methodBuilder.addParameter(paramBuilder.build());
        });

        TypeName typeName = TypeName.get(element.getReturnType());
        methodBuilder.returns(typeName);
        return methodBuilder;
    }
}