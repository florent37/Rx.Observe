package com.github.florent37.autoasync.processor;

import com.github.florent37.autoasync.processor.holders.Method;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import static javax.lang.model.element.ElementKind.CLASS;
import static javax.lang.model.element.ElementKind.INTERFACE;
import static javax.lang.model.element.ElementKind.METHOD;

/**
 * Created by florentchampigny on 10/03/2017.
 */

public class ProcessUtils {

    public List<Element> getMethods(Element element) {
        final List<? extends Element> enclosedElements = element.getEnclosedElements();
        final List<Element> mehods = new ArrayList<>();
        for (Element e : enclosedElements) {
            if (e.getKind() == METHOD)
                mehods.add(e);
        }
        return mehods;
    }

    public boolean isClassOrInterface(Element element){
        return element.getKind() == CLASS || element.getKind() == INTERFACE;
    }

    public ClassName fullName(Element element) {
        return ClassName.get((TypeElement) element);
    }

    public String className(Element element){
        return element.getSimpleName().toString();
    }

    public TypeName getParameterizedReturn(Element method){
        if(method.getKind() == METHOD) {
            ExecutableElement executableElement = (ExecutableElement) method;
            final TypeMirror returnType = executableElement.getReturnType();

            try {
                return ((ParameterizedTypeName) ParameterizedTypeName.get(returnType)).typeArguments.get(0);
            } catch (Exception e){
                return TypeName.get(returnType);
            }

        }
        return null;
    }

    public List<VariableElement> getParams(Element method){
        if(method.getKind() == METHOD) {
            ExecutableElement executableElement = (ExecutableElement) method;
            final List<VariableElement> parameters = (List<VariableElement>) executableElement.getParameters();
            return parameters;

        }
        return new ArrayList<>();
    }

    public boolean isVoid(TypeName returnType) {
        return returnType == TypeName.VOID || ClassName.get("java.lang", "Void").equals(returnType);
    }

    public boolean allMethodsAreStatic(Collection<Method> methods) {
        for (Method method : methods) {
            if(method.element.getKind() != METHOD || !method.element.getModifiers().contains(Modifier.STATIC)){
                return false;
            }
        }
        return true;
    }
}
