package com.github.florent37.autoasync.processor.holders;

import java.lang.annotation.Annotation;

import javax.lang.model.element.Element;

/**
 * Created by florentchampigny on 04/04/2017.
 */

public class Method {
    public final Element element;
    public final Class<? extends Annotation> annotation;

    public Method(Element element, Class<? extends Annotation> annotation) {
        this.element = element;
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Method method = (Method) o;

        if (element != null ? !element.equals(method.element) : method.element != null)
            return false;
        return annotation != null ? annotation.equals(method.annotation) : method.annotation == null;

    }

    @Override
    public int hashCode() {
        int result = element != null ? element.hashCode() : 0;
        result = 31 * result + (annotation != null ? annotation.hashCode() : 0);
        return result;
    }
}
