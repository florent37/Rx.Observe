package com.github.florent37.autoasync.processor;

import com.github.florent37.autoasync.processor.holders.ObserveHolder;
import com.github.florent37.rxobserve.annotations.Observe;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

@SupportedAnnotationTypes({
        "com.github.florent37.rxobserve.annotations.Observe",
})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
@AutoService(javax.annotation.processing.Processor.class)
public class RxObserveProcessor extends AbstractProcessor {

    private Map<ClassName, ObserveHolder> observeHolders = new HashMap<>();
    private Filer filer;
    private ProcessUtils processUtils = new ProcessUtils();

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        super.init(env);
        filer = env.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        processAnnotations(env);

        writeHoldersOnJavaFile();

        writeRxObserve();

        observeHolders.clear();

        return true;
    }

    private void writeRxObserve() {
        final TypeSpec.Builder builder = TypeSpec.classBuilder(Constants.CLASS)
                .addModifiers(Modifier.PUBLIC);

        for (ClassName className : observeHolders.keySet()) {
            final ObserveHolder observeHolder = observeHolders.get(className);
            final String simpleName = className.simpleName();
            final TypeName returnType = ClassName.bestGuess(className.packageName() + "." + simpleName + Constants.OBSERVE_CLASS);

            if (processUtils.allMethodsAreStatic(observeHolder.methods)) {
                builder.addMethod(MethodSpec.methodBuilder(Constants.METHOD_OF + simpleName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(returnType)
                        .addStatement("return new $T()", returnType)
                        .build());
            } else {
                builder.addMethod(MethodSpec.methodBuilder(Constants.METHOD_OF)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameter(className, Constants.TARGET)
                        .returns(returnType)
                        .addStatement("return new $T($L)", returnType, Constants.TARGET)
                        .build());
            }
        }

        final TypeSpec newClass = builder.build();

        final JavaFile javaFile = JavaFile.builder(Constants.PACKAGE, newClass).build();

        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processAnnotations(RoundEnvironment env) {
        for (Element element : env.getElementsAnnotatedWith(Observe.class)) {
            if (processUtils.isClassOrInterface(element)) {
                processObserveAllMethods(element);
            } else {
                processObserveAnnotated(element.getEnclosingElement());
            }
        }
    }

    private void processObserveAllMethods(Element element) {
        final ClassName classFullName = processUtils.fullName(element); //com.github.florent37.sample.MyModel
        final String className = processUtils.className(element); //MyModel

        final ObserveHolder observeHolder = new ObserveHolder(element, classFullName, className);

        for (Element method : processUtils.getMethods(element)) {
            observeHolder.addMethod(method);
        }

        observeHolders.put(classFullName, observeHolder);
    }

    private void processObserveAnnotated(Element element) {
        final ClassName classFullName = ClassName.get((TypeElement) element); //com.github.florent37.sample.TutoAndroidFrance
        if (!observeHolders.containsKey(classFullName)) {
            final String className = element.getSimpleName().toString(); //TutoAndroidFrance

            final ObserveHolder observeHolder = new ObserveHolder(element, classFullName, className);

            for (Element method : processUtils.getMethods(element)) {
                if (method.getAnnotation(Observe.class) != null) {
                    observeHolder.addMethod(method);
                }
            }

            observeHolders.put(classFullName, observeHolder);
        }
    }

    private void writeHoldersOnJavaFile() {
        for (ObserveHolder holder : observeHolders.values()) {
            construct(holder);
        }
    }

    public void construct(ObserveHolder observeHolder) {

        final TypeName target = observeHolder.classNameComplete;

        final TypeSpec.Builder builder = TypeSpec.classBuilder(observeHolder.className + Constants.OBSERVE_CLASS)
                .addModifiers(Modifier.PUBLIC);

        if (!processUtils.allMethodsAreStatic(observeHolder.methods)) {
            builder.addField(FieldSpec.builder(target, Constants.TARGET, Modifier.PRIVATE, Modifier.FINAL).build());
            builder.addMethod(MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(target, Constants.TARGET)
                    .addStatement("this.$L = $L", Constants.TARGET, Constants.TARGET)
                    .build());
        }


        for (Element method : observeHolder.methods) {

            final TypeName returnType = processUtils.getParameterizedReturn(method).box();

            if (processUtils.isVoid(returnType)) {

                final TypeVariableName typeVariableName = TypeVariableName.get("T");

                final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .addTypeVariable(typeVariableName)
                        .addParameter(typeVariableName, Constants.RETURNED_VALUE, Modifier.FINAL)
                        .returns(ParameterizedTypeName.get(ClassName.get(Observable.class), typeVariableName));

                final List<VariableElement> params = processUtils.getParams(method);
                final int paramsSize = params.size();

                for (int i = 0; i < paramsSize; i++) {
                    final VariableElement variableElement = params.get(i);
                    methodBuilder.addParameter(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString(), Modifier.FINAL);
                }

                methodBuilder
                        .addCode("return $T.create(new $T<$T>() {\n", ClassName.get(Observable.class), ClassName.get(ObservableOnSubscribe.class), typeVariableName)
                        .addCode("\t\t@$T\n", Override.class)
                        .addCode("\t\tpublic void subscribe($T<$T> e) throws $T {\n", ClassName.get(ObservableEmitter.class), typeVariableName, Exception.class);

                if (method.getModifiers().contains(Modifier.STATIC)) {
                    methodBuilder.addCode("\t\t\t$T.$L(", target, method.getSimpleName());
                } else {
                    methodBuilder.addCode("\t\t\t$L.$L(", Constants.TARGET, method.getSimpleName());
                }
                for (int i = 0; i < paramsSize; i++) {
                    final VariableElement variableElement = params.get(i);
                    methodBuilder.addCode("$L", variableElement.getSimpleName());
                    if (i < paramsSize - 1) {
                        methodBuilder.addCode(", ");
                    } else {
                        methodBuilder.addCode(");\n");
                    }
                }

                methodBuilder
                        .addStatement("\t\t\te.onNext(($T)$L)", typeVariableName, Constants.RETURNED_VALUE)
                        .addStatement("\t\t\te.onComplete()")
                        .addCode("\t\t}\n")
                        .addStatement("})");
                builder.addMethod(methodBuilder.build());
            } else {
                final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
                        .addModifiers(Modifier.PUBLIC)
                        .returns(ParameterizedTypeName.get(ClassName.get(Observable.class), returnType));

                final List<VariableElement> params = processUtils.getParams(method);
                final int paramsSize = params.size();

                for (int i = 0; i < paramsSize; i++) {
                    final VariableElement variableElement = params.get(i);
                    methodBuilder.addParameter(TypeName.get(variableElement.asType()), variableElement.getSimpleName().toString(), Modifier.FINAL);
                }

                methodBuilder
                        .addCode("return $T.create(new $T<$T>() {\n", ClassName.get(Observable.class), ClassName.get(ObservableOnSubscribe.class), returnType)
                        .addCode("\t\t@$T\n", Override.class)
                        .addCode("\t\tpublic void subscribe($T<$T> e) throws $T {\n", ClassName.get(ObservableEmitter.class), returnType, Exception.class);

                if (method.getModifiers().contains(Modifier.STATIC)) {
                    methodBuilder.addCode("\t\t\te.onNext($T.$L(", target, method.getSimpleName());
                } else {
                    methodBuilder.addCode("\t\t\te.onNext($L.$L(", Constants.TARGET, method.getSimpleName());
                }

                for (int i = 0; i < paramsSize; i++) {
                    final VariableElement variableElement = params.get(i);
                    methodBuilder.addCode("$L", variableElement.getSimpleName());
                    if (i < paramsSize - 1) {
                        methodBuilder.addCode(", ");
                    }
                }

                methodBuilder
                        .addCode("));\n")
                        .addStatement("\t\t\te.onComplete()")
                        .addCode("\t\t}\n")
                        .addStatement("})");
                builder.addMethod(methodBuilder.build());
            }
        }

        final TypeSpec newClass = builder.build();

        final JavaFile javaFile = JavaFile.builder(observeHolder.classNameComplete.packageName(), newClass).build();

        try {
            javaFile.writeTo(System.out);
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
