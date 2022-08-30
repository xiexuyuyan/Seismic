package org.yuyan.room.annotation.processor.dao;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import org.yuyan.room.annotation.processor.entity.ColumnType;
import org.yuyan.room.annotation.processor.entity.EntityType;
import org.yuyan.room.annotation.processor.entity.EntityHolder;
import org.yuyan.room.annotation.processor.utils.ClassUtils;
import org.yuyan.room.annotation.processor.utils.StringUtils;
import org.yuyan.room.dao.Delete;
import org.yuyan.room.dao.Insert;
import org.yuyan.room.dao.Query;
import org.yuyan.room.dao.Update;
import org.yuyan.room.entity.Entity;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurdMethod {
    /**
     * override public/protected fun method(param: Type): return{}
     * */
    public static MethodSpec.Builder baseBuilder(ExecutableElement element) {
        String methodName = element.getSimpleName().toString();
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodName);

        ClassName override = ClassName.get("java.lang", "Override");
        methodBuilder.addAnnotation(override);

        element.getModifiers().forEach(modifier -> {
            switch (modifier) {
                case PUBLIC:
                case PROTECTED:
                    methodBuilder.addModifiers(modifier);
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


    /**
     * the best universal way to insert singleton Entity object
     * @Insert (entity = User::class)
     * fun insert(user: User)
     *
     * so, usual we build the base method in CurdMethod.baseBuilder()
     * thus we just to create the statement with annotation info
     * */
    public static void setBasicInsertion(MethodSpec.Builder methodBuilder
            , ExecutableElement element) {
        String entityCanonicalName = getEntityCanonicalName(element);
        EntityType entityType = EntityHolder.entities.get(entityCanonicalName);
        if (entityType == null || entityCanonicalName == null) {
            return;
        }

        // --- info from param
        String paramName = null;
        for (VariableElement parameter : element.getParameters()) {
            TypeName clsType = TypeName.get(parameter.asType());
            if (clsType.toString().equals(entityCanonicalName)) {
                paramName = parameter.getSimpleName().toString();
                break;
            }
        }
        if (paramName == null) {
            return;
        }

        List<ColumnType> columns = entityType.getColumns();

        String finalParamName = paramName;
        new Batch(methodBuilder){
            @Override
            void action() {
                methodBuilder.addCode("    String sql = \"insert into \" + \n");
                methodBuilder.addCode("        \"`$N`(", entityType.getTableName());
                for (int i = 0; i < columns.size(); i++) {
                    if (i < columns.size() - 1) {
                        methodBuilder.addCode("`$N`, ", columns.get(i).getColumnName());
                    } else {
                        methodBuilder.addCode("`$N`) \" + \n", columns.get(i).getColumnName());
                    }
                }
                methodBuilder.addCode("        \"values(\" + \n");
                for (int i = 0; i < columns.size(); i++) {
                    String methodName = StringUtils.upperCase(columns.get(i).getVarName());
                    if (i < columns.size() - 1) {
                        methodBuilder.addCode("            \"'\" + $N.get$N() + \"', \" + \n", finalParamName, methodName);
                    } else {
                        methodBuilder.addCode("            \"'\" + $N.get$N() + \"')\";\n", finalParamName, methodName);
                    }
                }
                methodBuilder.addCode("    System.out.println(sql);\n");
                methodBuilder.addCode("    int rowCount = statement.executeUpdate(\n");
                methodBuilder.addCode("        sql, $T.RETURN_GENERATED_KEYS);\n", Statement.class);
                methodBuilder.addCode("    System.out.println(\"affected line size = \" + rowCount);\n");
                methodBuilder.addCode("    $T resultSet = statement.getGeneratedKeys();\n", ResultSet.class);
                methodBuilder.addCode("    resultSet.close();\n");
            }
        }.build();
    }

    private static void setBasicUpdateMethod(MethodSpec.Builder methodBuilder
            , ExecutableElement element) {
        String entityCanonicalName = getEntityCanonicalName(element);
        EntityType entityType = EntityHolder.entities.get(entityCanonicalName);
        if (entityType == null || entityCanonicalName == null) {
            return;
        }

        // --- info from param
        String paramName = null;
        for (VariableElement parameter : element.getParameters()) {
            TypeName clsType = TypeName.get(parameter.asType());
            if (clsType.toString().equals(entityCanonicalName)) {
                paramName = parameter.getSimpleName().toString();
                break;
            }
        }
        if (paramName == null) {
            return;
        }

        List<ColumnType> columns = entityType.getColumns();

        String finalParamName = paramName;
        new Batch(methodBuilder){
            @Override
            void action() {
                methodBuilder.addCode("    String sql = \"update $N \"\n", entityType.getTableName());
                String primaryVarName = "";
                String dot = "set";
                for (ColumnType columnType : columns) {
                    String columnName = columnType.getColumnName();
                    if (columnName.equals(entityType.getPrimaryKey())) {
                        primaryVarName = columnType.getVarName();
                        continue;
                    }
                    String varGetMethod = StringUtils.upperCase(columnType.getVarName());
                    methodBuilder.addCode("               + \"$N $N = '\" + $N.get$N() + \"' \"\n", dot,columnName, finalParamName, varGetMethod);
                    dot = ",";
                }
                String primaryKeyGetMethod = StringUtils.upperCase(primaryVarName);
                methodBuilder.addCode("               + \"where $N = \" + $N.get$N();\n", entityType.getPrimaryKey(), finalParamName, primaryKeyGetMethod);
                methodBuilder.addCode("    System.out.println(sql);\n");
                methodBuilder.addCode("    int rowCount = statement.executeUpdate(\n");
                methodBuilder.addCode("        sql, $T.RETURN_GENERATED_KEYS);\n", Statement.class);
                methodBuilder.addCode("    System.out.println(\"affected line size = \" + rowCount);\n");
                methodBuilder.addCode("    $T resultSet = statement.getGeneratedKeys();\n", ResultSet.class);
                methodBuilder.addCode("    resultSet.close();\n");
            }
        }.build();

        /**
         * String sql = update users set user_name = '', user_mail = '' where uid = ''
         * */

    }

    /**
     * the universal way to do a query
     * @Query (val entity: KClass<*> = Any::class, val statement: String)
     * */
    public static void setBasicQueryMethod(MethodSpec.Builder methodBuilder
            , ExecutableElement element) {
        Query query = element.getAnnotation(Query.class);
        String statement = query.statement();
        if (statement.length() == 0) {
            return;
        }
        System.out.println("query statement: " + statement);
        methodBuilder.addCode("$T ret = new $T();\n", element.getReturnType(), element.getReturnType());
        new Batch(methodBuilder){
            @Override
            void action() {
                methodBuilder.addCode("    String sql = $S;\n", statement);
                element.getParameters().forEach(variableElement -> {
                    String paramName = variableElement.getSimpleName().toString();
                    methodBuilder.addCode("    sql = sql.replace(\":" + paramName +"\", \"'\"+$N+\"'\");\n", paramName);
                });
                methodBuilder.addCode("    System.out.println(sql);\n");
                methodBuilder.addCode("    ResultSet rs = statement.executeQuery(sql);\n");
                methodBuilder.addCode("    while(rs.next()){\n");

                String retTypeName = element.getReturnType().toString();
                EntityType retType = EntityHolder.entities.get(retTypeName);
                List<ColumnType> columnTypeList = retType.getColumns();
                columnTypeList.forEach(columnType -> {
                    String getter = ClassUtils.classGetter(columnType.getVarType().toString());
                    methodBuilder.addCode("        $T _$N = rs.$L(\"$L\");\n"
                            , TypeName.get(columnType.getVarType())
                            , columnType.getVarName()
                            , getter
                            , columnType.getColumnName()
                    );
                    String setter = "set" + StringUtils.upperCase(columnType.getVarName());
                    methodBuilder.addCode("        ret.$L($L);\n", setter, "_" + columnType.getVarName());
                });
                methodBuilder.addCode("    }\n");
                methodBuilder.addCode("    rs.close();\n");
                /**
                 * String sql = $statement;
                 * for element.variable
                 *     sql = sql.replace(varName, $N);
                 * ResultSet rs = stmt.executeQuery(sql);
                 * while(rs.next()){
                 *     int id  = rs.getInt("id");
                 *     String name = rs.getString("name");
                 *     String url = rs.getString("url");
                 * }
                 *
                 * */
            }
        }.build();
        methodBuilder.addStatement("return ret");
    }

    public static void setBasicCurdMethod(MethodSpec.Builder methodBuilder
            , ExecutableElement element, Class<? extends Annotation> aClass){
        if (aClass.equals(Insert.class)) {
            setBasicInsertion(methodBuilder, element);
        } else if (aClass.equals(Delete.class)) {

        } else if (aClass.equals(Update.class)) {
            setBasicUpdateMethod(methodBuilder, element);
        } else if (aClass.equals(Query.class)) {
            setBasicQueryMethod(methodBuilder, element);
        }
    }

    public static boolean isCurdMethod(String canonical){
        List<String> curdList = new ArrayList<>();
        curdList.add(Insert.class.getCanonicalName());
        curdList.add(Delete.class.getCanonicalName());
        curdList.add(Update.class.getCanonicalName());
        curdList.add(Query.class.getCanonicalName());
        for (String s : curdList) {
            if (s.equals(canonical)) {
                return true;
            }
        }
        return false;
    }
    private static String getEntityCanonicalName(ExecutableElement element){
        String entityCanonicalName = null;

        AnnotationMirror curdMirror = null;
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            String annotationTypeName = annotationMirror.getAnnotationType().toString();
            if (isCurdMethod(annotationTypeName)) {
                curdMirror = annotationMirror;
            }
        }

        if (curdMirror == null) {
            return null;
        }

        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
                : curdMirror.getElementValues().entrySet()) {
            String annotationMethodName = entry.getKey().getSimpleName().toString();
            // todo(make it "entity" to be a global constant)
            if ("entity".equals(annotationMethodName)) {
                entityCanonicalName = entry.getValue().getValue().toString();
                break;
            }
        }

        return entityCanonicalName;
    }


    private abstract static class Batch{
        private final MethodSpec.Builder methodBuilder;

        public Batch(MethodSpec.Builder methodBuilder) {
            this.methodBuilder = methodBuilder;
        }

        abstract void action();

        void build(){
            prefix();
            action();
            suffix();
        }

        private void prefix(){
            methodBuilder.addCode("$T connection = null;\n", Connection.class);
            methodBuilder.addCode("$T statement = null;\n", Statement.class);
            methodBuilder.addCode("try{\n");
            methodBuilder.addCode("    connection = configure.createConnection();\n");
            methodBuilder.addCode("    statement = connection.createStatement();\n");
        }
        private void suffix(){
            methodBuilder.addCode("    statement.close();\n");
            methodBuilder.addCode("    connection.close();\n");
            methodBuilder.addCode("} catch ($T e) {\n", SQLException.class);
            methodBuilder.addCode("    e.printStackTrace();\n");
            methodBuilder.addCode("} finally {\n");
            methodBuilder.addCode("    try{\n");
            methodBuilder.addCode("        if (statement != null)\n");
            methodBuilder.addCode("            statement.close();\n");
            methodBuilder.addCode("        if (connection != null)\n");
            methodBuilder.addCode("            connection.close();\n");
            methodBuilder.addCode("    } catch ($T e) {\n", SQLException.class);
            methodBuilder.addCode("        e.printStackTrace();\n");
            methodBuilder.addCode("    }\n");
            methodBuilder.addCode("}\n");
        }
    }
}
