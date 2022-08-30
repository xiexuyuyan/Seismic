package org.yuyan.room.annotation.processor.dao;

import com.squareup.javapoet.TypeSpec;

public interface DaoProcessable {
    TypeSpec.Builder createContainerClassBuilder(String pkgName, String clsName);
}
