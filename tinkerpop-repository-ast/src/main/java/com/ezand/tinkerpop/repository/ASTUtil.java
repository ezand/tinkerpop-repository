package com.ezand.tinkerpop.repository;

import static com.ezand.tinkerpop.repository.Names.splitNameOf;
import static com.sun.tools.javac.tree.JCTree.JCAnnotation;
import static com.sun.tools.javac.tree.JCTree.JCExpression;
import static com.sun.tools.javac.tree.JCTree.JCNewClass;
import static com.sun.tools.javac.tree.JCTree.JCTypeApply;
import static com.sun.tools.javac.tree.JCTree.JCVariableDecl;
import static com.sun.tools.javac.util.List.nil;
import static com.tinkerpop.gremlin.util.StreamFactory.stream;
import static java.util.stream.Collectors.toSet;
import static lombok.core.AST.Kind.FIELD;
import static lombok.core.AST.Kind.METHOD;
import static lombok.javac.handlers.JavacHandlerUtil.MemberExistsResult.NOT_EXISTS;
import static lombok.javac.handlers.JavacHandlerUtil.chainDots;
import static lombok.javac.handlers.JavacHandlerUtil.toGetterName;
import static lombok.javac.handlers.JavacHandlerUtil.toSetterName;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.javac.JavacNode;
import lombok.javac.JavacTreeMaker;
import lombok.javac.handlers.JavacHandlerUtil;

import com.sun.tools.javac.util.List;

public class ASTUtil {
    public static boolean getterExists(JavacNode enclosing, JavacNode field) {
        return methodExists(toGetterName(field), enclosing, 0);
    }

    public static boolean setterExists(JavacNode enclosing, JavacNode field) {
        return methodExists(toSetterName(field), enclosing, 1);
    }

    public static boolean methodExists(String methodName, JavacNode node, int parameterCount) {
        return !JavacHandlerUtil.methodExists(methodName, node, parameterCount).equals(NOT_EXISTS);
    }

    public static Object getAnnotationParameterValue(JCAnnotation annotation, String parameterName) {
        return annotation.attribute.getElementValues().entrySet()
                .stream()
                .filter(e -> e.getKey().name.toString().equals(parameterName))
                .map(e -> e.getValue().getValue())
                .findFirst()
                .orElse(null);
    }

    public static Set<JavacNode> getFields(JavacNode typeNode) {
        return stream(typeNode.down())
                .filter(n -> n.getKind().equals(FIELD))
                .collect(toSet());
    }

    public static Set<JavacNode> getAccessorFields(JavacNode typeNode) {
        return stream(typeNode.down())
                .filter(n -> n.getKind().equals(FIELD)
                        && getterExists(typeNode, n)
                        && setterExists(typeNode, n))
                .collect(toSet());
    }

    public static Set<JavacNode> getMethods(JavacNode typeNode) {
        return stream(typeNode.down())
                .filter(n -> n.getKind().equals(METHOD))
                .collect(toSet());
    }

    public static JavacNode findGetter(JavacNode typeNode, JavacNode fieldNode) {
        return getMethods(typeNode)
                .stream()
                .filter(m -> m.getName().equals(toGetterName(fieldNode)))
                .findFirst()
                .orElse(null);
    }

    public static JavacNode findSetter(JavacNode typeNode, JavacNode fieldNode) {
        return getMethods(typeNode)
                .stream()
                .filter(m -> m.getName().equals(toSetterName(fieldNode)))
                .findFirst()
                .orElse(null);
    }

    public static JCNewClass newClass(JavacNode typeNode, Class<?> clazz, List<Class<?>> types, JCExpression... args) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCExpression expression = chainDots(typeNode, splitNameOf(clazz));

        if (types != null && types.isEmpty()) {
            expression = maker.TypeApply(expression, nil());
        }

        return maker.NewClass(null, types == null ? nil() : toExpressions(typeNode, types.toArray(new Class[types.length()])), expression, args != null ? List.from(args) : nil(), null);
    }

    public static JCVariableDecl newVariable(JavacNode typeNode, String variableName, JCExpression initialValue, List<Class<?>> types) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        JCExpression type = chainDots(typeNode, splitNameOf(Set.class));
        return maker.VarDef(maker.Modifiers(0), typeNode.toName(variableName), types == null ? type : typed(typeNode, type, types.toArray(new Class[types.length()])), initialValue);
    }

    public static JCTypeApply typed(JavacNode typeNode, JCExpression target, Class<?>... classes) {
        JavacTreeMaker maker = typeNode.getTreeMaker();
        return maker.TypeApply(target, toExpressions(typeNode, classes));
    }

    private static List<JCExpression> toExpressions(JavacNode typeNode, Class<?>... classes) {
        return List.from(Arrays.stream(classes)
                .map(c -> chainDots(typeNode, splitNameOf(c)))
                .collect(Collectors.toList()));
    }
}