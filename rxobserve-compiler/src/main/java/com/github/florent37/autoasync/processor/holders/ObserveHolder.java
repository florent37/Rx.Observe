package com.github.florent37.autoasync.processor.holders;

import com.squareup.javapoet.ClassName;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;

public class ObserveHolder {
    public Element element;
    public ClassName classNameComplete;
    public String className;
    public Set<Method> methods;

    public ObserveHolder(Element element, ClassName classNameComplete, String className) {
        this.element = element;
        this.classNameComplete = classNameComplete;
        this.className = className;
        this.methods = new HashSet<>();
    }

    public void addMethod(Element child, Class<? extends Annotation> annotation){
        methods.add(new Method(child, annotation));
    }

}
