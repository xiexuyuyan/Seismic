package org.yuyan.room.annotation.processor.database;


import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Map;

public interface DatabaseProcessable {
    TypeSpec.Builder createContainerClassBuilder(String pkgName, String clsName);
    Map<FieldSpec, MethodSpec> createDaoReference(Element elementAnnotatedDatabase);
}
