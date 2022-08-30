package org.yuyan.room.annotation.processor.entity;

import org.yuyan.room.annotation.processor.helper.AnnotationProcessorHelper;
import org.yuyan.room.entity.ColumnInfo;
import org.yuyan.room.entity.Entity;
import org.yuyan.room.entity.PrimaryKey;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import java.util.*;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class EntityAnnotationProcessor extends AbstractProcessor implements EntityProcessable{
    private final List<Class<?>> supportClassList = new ArrayList<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        supportClassList.add(Entity.class);
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
        roundEnv.getElementsAnnotatedWith(Entity.class).forEach(elementAnnotatedEntity -> {
            String entityCanonicalName = elementAnnotatedEntity.asType().toString();
            System.out.print("entity handle: entity= " + entityCanonicalName + ", ");
            EntityType entityType = new EntityType();
            entityType.tableName = elementAnnotatedEntity.getAnnotation(Entity.class).tableName();
            System.out.println("table name: " + entityType.tableName);
            ColumnType columnType = null;
            for (VariableElement variableElement : ElementFilter.fieldsIn(elementAnnotatedEntity.getEnclosedElements())) {
                String varName = variableElement.getSimpleName().toString();
                TypeMirror varType= variableElement.asType();
                String columnName = null;

                ColumnInfo columnInfo = variableElement.getAnnotation(ColumnInfo.class);
                if (columnInfo != null) {
                    columnName = columnInfo.name();
                } else {
                    continue;
                }

                PrimaryKey primaryKey = variableElement.getAnnotation(PrimaryKey.class);
                if (primaryKey != null) {
                    entityType.primaryKey = columnName;
                }

                columnType = new ColumnType(columnName, varName, varType);
                entityType.columns.add(columnType);
            }
            EntityHolder.entities.put(entityCanonicalName, entityType);
        });
        return false;
    }

    @Override
    public void getTableName() {

    }
}