/*
 * Copyright (C) 2015 8tory, Inc.
 * Copyright (C) 2012 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retroweibo.processor;

import retroweibo.RetroWeibo;
import com.google.auto.service.AutoService;
import com.google.common.base.Functions;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.beans.Introspector;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.annotation.Generated;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Javac annotation processor (compiler plugin) for value types; user code never references this
 * class.
 *
 * @author Éamonn McManus
 * @see retroweibo.RetroWeibo
 */
@AutoService(Processor.class)
public class RetroWeiboProcessor extends AbstractProcessor {
  public RetroWeiboProcessor() {
  }

  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.of(RetroWeibo.class.getName());
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  private ErrorReporter errorReporter;

  /**
   * Qualified names of {@code @RetroWeibo} classes that we attempted to process but had to abandon
   * because we needed other types that they referenced and those other types were missing.
   */
  private final List<String> deferredTypeNames = new ArrayList<String>();

  @Override
  public synchronized void init(ProcessingEnvironment processingEnv) {
    super.init(processingEnv);
    errorReporter = new ErrorReporter(processingEnv);
  }

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    List<TypeElement> deferredTypes = new ArrayList<TypeElement>();
    for (String deferred : deferredTypeNames) {
      deferredTypes.add(processingEnv.getElementUtils().getTypeElement(deferred));
    }
    if (roundEnv.processingOver()) {
      // This means that the previous round didn't generate any new sources, so we can't have found
      // any new instances of @RetroWeibo; and we can't have any new types that are the reason a type
      // was in deferredTypes.
      for (TypeElement type : deferredTypes) {
        errorReporter.reportError("Did not generate @RetroWeibo class for " + type.getQualifiedName()
            + " because it references undefined types", type);
      }
      return false;
    }
    Collection<? extends Element> annotatedElements =
        roundEnv.getElementsAnnotatedWith(RetroWeibo.class);
    List<TypeElement> types = new ImmutableList.Builder<TypeElement>()
        .addAll(deferredTypes)
        .addAll(ElementFilter.typesIn(annotatedElements))
        .build();
    deferredTypeNames.clear();
    for (TypeElement type : types) {
      try {
        processType(type);
      } catch (AbortProcessingException e) {
        // We abandoned this type; continue with the next.
      } catch (MissingTypeException e) {
        // We abandoned this type, but only because we needed another type that it references and
        // that other type was missing. It is possible that the missing type will be generated by
        // further annotation processing, so we will try again on the next round (perhaps failing
        // again and adding it back to the list). We save the name of the @RetroWeibo type rather
        // than its TypeElement because it is not guaranteed that it will be represented by
        // the same TypeElement on the next round.
        deferredTypeNames.add(type.getQualifiedName().toString());
      } catch (RuntimeException e) {
        // Don't propagate this exception, which will confusingly crash the compiler.
        // Instead, report a compiler error with the stack trace.
        String trace = Throwables.getStackTraceAsString(e);
        errorReporter.reportError("@RetroWeibo processor threw an exception: " + trace, type);
      }
    }
    return false;  // never claim annotation, because who knows what other processors want?
  }

  private String generatedClassName(TypeElement type, String prefix) {
    String name = type.getSimpleName().toString();
    while (type.getEnclosingElement() instanceof TypeElement) {
      type = (TypeElement) type.getEnclosingElement();
      name = type.getSimpleName() + "_" + name;
    }
    String pkg = TypeSimplifier.packageNameOf(type);
    String dot = pkg.isEmpty() ? "" : ".";
    return pkg + dot + prefix + name;
  }

  private String generatedSubclassName(TypeElement type) {
    return generatedClassName(type, "RetroWeibo_");
  }

  public interface Action1<T> {
      void call(T t);
  }

  private void onAnnotationForProperty(AnnotationMirror annotation) {
      onAnnotationForProperty.call(annotation);
  }

  private Action1<? super AnnotationMirror> onAnnotationForProperty;

  private void annotationForProperty(Action1<? super AnnotationMirror> onAnnotationForProperty) {
      this.onAnnotationForProperty = onAnnotationForProperty;
  }

  /**
   * A property of an {@code @RetroWeibo} class, defined by one of its abstract methods.
   * An instance of this class is made available to the Velocity template engine for
   * each property. The public methods of this class define JavaBeans-style properties
   * that are accessible from templates. For example {@link #getType()} means we can
   * write {@code $p.type} for a Velocity variable {@code $p} that is a {@code Property}.
   */
  public static class Property {
    private final String name;
    private final String identifier;
    private final ExecutableElement method;
    private final String type;
    private String typeArgs;
    private final ImmutableList<String> annotations;
    private final String args;
    private final String path;
    private final Map<String, String> queries;
    private final List<String> queryMaps;
    private final List<String> queryBundles;
    private final boolean isGet;
    private final boolean isPost;
    private final boolean isDelete;
    private final String body;
    private final String callbackType;
    private final String callbackArg;
    private final ProcessingEnvironment processingEnv;
    private final TypeSimplifier typeSimplifier;
    private final List<String> permissions;

    Property(
        String name,
        String identifier,
        ExecutableElement method,
        String type,
        TypeSimplifier typeSimplifier,
        ProcessingEnvironment processingEnv
        ) {
      this.name = name;
      this.identifier = identifier;
      this.method = method;
      this.type = type;
      this.typeSimplifier = typeSimplifier;
      this.processingEnv = processingEnv;
      this.annotations = buildAnnotations(typeSimplifier);
      this.args = formalTypeArgsString(method);
      this.path = buildPath(method);
      this.typeArgs = buildTypeArguments(type);
      this.queries = buildQueries(method);
      this.queryMaps = buildQueryMaps(method);
      this.queryBundles = buildQueryBundles(method);
      this.isGet = buildIsGet(method);
      this.isPost = buildIsPost(method);
      this.isDelete = buildIsDelete(method);
      this.body = buildBody(method);
      this.callbackType = buildCallbackType(method);
      this.callbackArg = buildCallbackArg(method);
      if ("".equals(typeArgs)) typeArgs = callbackType;
      this.permissions = buildPermissions(method);
    }

    private String buildTypeArguments(String type) {
      Pattern pattern = Pattern.compile( "<(.*?)>" );
      Matcher m = pattern.matcher(type);
      if (m.find()) return m.group(1);
      return "";
    }


    public String buildCallbackArg(ExecutableElement method) {
        return "callback"; // TODO
    }

    public String buildCallbackType(ExecutableElement method) {
      Types typeUtils = processingEnv.getTypeUtils();
      TypeMirror callback = getTypeMirror(processingEnv, RetroWeibo.Callback.class);

      List<? extends VariableElement> parameters = method.getParameters();
      for (VariableElement parameter : parameters) {
        TypeMirror type = parameter.asType();
        if (type instanceof DeclaredType) {
          List<? extends TypeMirror> params = ((DeclaredType) type).getTypeArguments();
          if (params.size() == 1) {
            callback = typeUtils.getDeclaredType((TypeElement) typeUtils
                    .asElement(callback), new TypeMirror[] {params.get(0)});

            if (typeUtils.isSubtype(type, callback)) {
              return typeSimplifier.simplify(params.get(0));
            }
          }
        }
      }
      return "";
    }

    public boolean buildIsGet(ExecutableElement method) {
        // TODO duplicated routine
        return method.getAnnotation(retroweibo.RetroWeibo.GET.class) != null;
    }

    public boolean buildIsPost(ExecutableElement method) {
        // TODO duplicated routine
        return method.getAnnotation(retroweibo.RetroWeibo.POST.class) != null;
    }

    public boolean buildIsDelete(ExecutableElement method) {
        // TODO duplicated routine
        return method.getAnnotation(retroweibo.RetroWeibo.DELETE.class) != null;
    }

    public String buildBody(ExecutableElement method) {
      String body = "";

      // TODO duplicated routine
      retroweibo.RetroWeibo.POST post = method.getAnnotation(retroweibo.RetroWeibo.POST.class);
      if (post == null) return body;

      // TODO duplicated code
      List<? extends VariableElement> parameters = method.getParameters();
      for (VariableElement parameter : parameters) {
        if (parameter.getAnnotation(retroweibo.RetroWeibo.Body.class) != null) {
          body = parameter.getSimpleName().toString();
        }
      }
      return body;
    }

    public List<String> buildPermissions(ExecutableElement method) {
      retroweibo.RetroWeibo.GET get = method.getAnnotation(retroweibo.RetroWeibo.GET.class);
      retroweibo.RetroWeibo.POST post = method.getAnnotation(retroweibo.RetroWeibo.POST.class);
      retroweibo.RetroWeibo.DELETE delete = method.getAnnotation(retroweibo.RetroWeibo.DELETE.class);
      if (get != null) return Arrays.asList(get.permissions());
      if (post != null) return Arrays.asList(post.permissions());
      if (delete != null) return Arrays.asList(delete.permissions());
      return Collections.emptyList();
    }

    // /{postId}
    // /{userIdA}/friends/{userIdB}
    // "/" + userIdA + "/friends/" + userIdB
    // "/" + userIdA + "/friends/" + userIdB + ""
    public String buildPath(ExecutableElement method) {
      // TODO duplicated routine
      retroweibo.RetroWeibo.GET get = method.getAnnotation(retroweibo.RetroWeibo.GET.class);
      retroweibo.RetroWeibo.POST post = method.getAnnotation(retroweibo.RetroWeibo.POST.class);
      retroweibo.RetroWeibo.DELETE delete = method.getAnnotation(retroweibo.RetroWeibo.DELETE.class);
      String fullPath = null;
      if (get != null) fullPath = get.value();
      if (post != null) fullPath = post.value();
      if (delete != null) fullPath = delete.value();

      List<? extends VariableElement> parameters = method.getParameters();
      for (VariableElement parameter : parameters) {
        retroweibo.RetroWeibo.Path path = parameter
            .getAnnotation(retroweibo.RetroWeibo.Path.class);
        if ((path != null) && (!path.value().equals("null"))) {
          fullPath = fullPath.replace("{" + path.value() + "}", "\" + " +
              parameter.getSimpleName().toString() + " + \"");
        } else {
          fullPath = fullPath.replace("{" + parameter.getSimpleName().toString() + "}", "\" + " +
              parameter.getSimpleName().toString() + " + \"");
        }
      }

      return "\"" + fullPath.replaceAll("\\?.+", "") + "\"";
    }

    public Map<String, String> buildQueries(ExecutableElement method) {
      Map<String, String> map = new HashMap<String, String>();

      // TODO duplicated routine
      retroweibo.RetroWeibo.GET get = method.getAnnotation(retroweibo.RetroWeibo.GET.class);
      retroweibo.RetroWeibo.POST post = method.getAnnotation(retroweibo.RetroWeibo.POST.class);
      retroweibo.RetroWeibo.DELETE delete = method.getAnnotation(retroweibo.RetroWeibo.DELETE.class);
      String fullPath = null;
      if (get != null) fullPath = get.value();
      if (post != null) fullPath = post.value();
      if (delete != null) fullPath = delete.value();

      if (fullPath.indexOf("?") != -1) {
        fullPath = fullPath.replaceAll("^.*\\?", "");
        String[] queries = fullPath.split("&");
        for (String query : queries) {
          String[] keyValue = query.split("=");
          map.put("\"" + keyValue[0] + "\"", "\"" + keyValue[1] + "\"");
        }
      }

      List<? extends VariableElement> parameters = method.getParameters();
      for (VariableElement parameter : parameters) {
        retroweibo.RetroWeibo.Query query = parameter
            .getAnnotation(retroweibo.RetroWeibo.Query.class);
        if (query == null) {
          continue;
        }

        if (!query.value().equals("null")) {
          map.put("\"" + query.value() + "\"", parameter.getSimpleName().toString());
        } else {
          map.put("\"" + parameter.getSimpleName().toString() + "\"",
              parameter.getSimpleName().toString());
        }
      }

      return map;
    }

    public List<String> buildQueryMaps(ExecutableElement method) {
      List<String> queryMaps = new ArrayList<String>();
      List<? extends VariableElement> parameters = method.getParameters();
      for (VariableElement parameter : parameters) {
        retroweibo.RetroWeibo.QueryMap queryMap = parameter
            .getAnnotation(retroweibo.RetroWeibo.QueryMap.class);
        if (queryMap == null) {
          continue;
        }

        queryMaps.add(parameter.getSimpleName().toString());
      }
      return queryMaps;
    }

    public List<String> buildQueryBundles(ExecutableElement method) {
      List<String> queryBundles = new ArrayList<String>();
      List<? extends VariableElement> parameters = method.getParameters();
      for (VariableElement parameter : parameters) {
        retroweibo.RetroWeibo.QueryBundle queryBundle = parameter
            .getAnnotation(retroweibo.RetroWeibo.QueryBundle.class);
        if (queryBundle == null) {
          continue;
        }

        queryBundles.add(parameter.getSimpleName().toString());
      }
      return queryBundles;
    }

    private ImmutableList<String> buildAnnotations(TypeSimplifier typeSimplifier) {
      ImmutableList.Builder<String> builder = ImmutableList.builder();

      for (AnnotationMirror annotationMirror : method.getAnnotationMirrors()) {
        TypeElement annotationElement =
            (TypeElement) annotationMirror.getAnnotationType().asElement();
        if (annotationElement.getQualifiedName().toString().equals(Override.class.getName())) {
          // Don't copy @Override if present, since we will be adding our own @Override in the
          // implementation.
          continue;
        }
        // TODO(user): we should import this type if it is not already imported
        AnnotationOutput annotationOutput = new AnnotationOutput(typeSimplifier);
        builder.add(annotationOutput.sourceFormForAnnotation(annotationMirror));
      }

      return builder.build();
    }

    /**
     * Returns the name of the property as it should be used when declaring identifiers (fields and
     * parameters). If the original getter method was {@code foo()} then this will be {@code foo}.
     * If it was {@code getFoo()} then it will be {@code foo}. If it was {@code getPackage()} then
     * it will be something like {@code package0}, since {@code package} is a reserved word.
     */
    @Override
    public String toString() {
      return identifier;
    }

    /**
     * Returns the name of the property as it should be used in strings visible to users. This is
     * usually the same as {@code toString()}, except that if we had to use an identifier like
     * "package0" because "package" is a reserved word, the name here will be the original
     * "package".
     */
    public String getName() {
      return name;
    }

    /**
     * Returns the name of the getter method for this property as defined by the {@code @RetroWeibo}
     * class. For property {@code foo}, this will be {@code foo} or {@code getFoo} or {@code isFoo}.
     */
    public String getGetter() {
      return method.getSimpleName().toString();
    }

    TypeElement getOwner() {
      return (TypeElement) method.getEnclosingElement();
    }

    TypeMirror getReturnType() {
      return method.getReturnType();
    }

    public String getType() {
      return type;
    }

    public String getTypeArgs() {
      return typeArgs;
    }

    public TypeKind getKind() {
      return method.getReturnType().getKind();
    }

    public String getCastType() {
      return primitive() ? box(method.getReturnType().getKind()) : getType();
    }

    private String box(TypeKind kind) {
      switch (kind) {
        case BOOLEAN:
          return "Boolean";
        case BYTE:
          return "Byte";
        case SHORT:
          return "Short";
        case INT:
          return "Integer";
        case LONG:
          return "Long";
        case CHAR:
          return "Character";
        case FLOAT:
          return "Float";
        case DOUBLE:
          return "Double";
        default:
          throw new RuntimeException("Unknown primitive of kind " + kind);
        }
    }

    public boolean primitive() {
      return method.getReturnType().getKind().isPrimitive();
    }

    public boolean isCallback() {
      return (callbackType != null && !"".equals(callbackType));
    }

    public String getCallbackType() {
      return callbackType;
    }

    public String getCallbackArg() {
      return callbackArg;
    }

    public String getBody() {
      return body;
    }

    public List<String> getPermissions() {
      return permissions;
    }

    public boolean isGet() {
      return isGet;
    }

    public boolean isPost() {
      return isPost;
    }

    public boolean isDelete() {
      return isDelete;
    }

    public List<String> getAnnotations() {
      return annotations;
    }

    public String getArgs() {
      return args;
    }

    public String getPath() {
      return path;
    }

    public Map<String, String> getQueries() {
      return queries;
    }

    public List<String> getQueryMaps() {
      return queryMaps;
    }

    public List<String> getQueryBundles() {
      return queryBundles;
    }

    public boolean isNullable() {
      for (AnnotationMirror annotationMirror : method.getAnnotationMirrors()) {
        String name = annotationMirror.getAnnotationType().asElement().getSimpleName().toString();
        if (name.equals("Nullable")) {
          return true;
        }
      }
      return false;
    }

    public String getAccess() {
      Set<Modifier> mods = method.getModifiers();
      if (mods.contains(Modifier.PUBLIC)) {
        return "public ";
      } else if (mods.contains(Modifier.PROTECTED)) {
        return "protected ";
      } else {
        return "";
      }
    }
  }

  private static boolean isJavaLangObject(TypeElement type) {
    return type.getSuperclass().getKind() == TypeKind.NONE && type.getKind() == ElementKind.CLASS;
  }

  private enum ObjectMethodToOverride {
    NONE, TO_STRING, EQUALS, HASH_CODE, DESCRIBE_CONTENTS, WRITE_TO_PARCEL
  }

  private static ObjectMethodToOverride objectMethodToOverride(ExecutableElement method) {
    String name = method.getSimpleName().toString();
    switch (method.getParameters().size()) {
      case 0:
        if (name.equals("toString")) {
          return ObjectMethodToOverride.TO_STRING;
        } else if (name.equals("hashCode")) {
          return ObjectMethodToOverride.HASH_CODE;
        } else if (name.equals("describeContents")) {
          return ObjectMethodToOverride.DESCRIBE_CONTENTS;
        }
        break;
      case 1:
        if (name.equals("equals")
            && method.getParameters().get(0).asType().toString().equals("java.lang.Object")) {
          return ObjectMethodToOverride.EQUALS;
        }
        break;
      case 2:
        if (name.equals("writeToParcel")
            && method.getParameters().get(0).asType().toString().equals("android.os.Parcel")
            && method.getParameters().get(1).asType().toString().equals("int")) {
          return ObjectMethodToOverride.WRITE_TO_PARCEL;
        }
        break;
    }
    return ObjectMethodToOverride.NONE;
  }

  private void findLocalAndInheritedMethods(TypeElement type, List<ExecutableElement> methods) {
    Types typeUtils = processingEnv.getTypeUtils();
    Elements elementUtils = processingEnv.getElementUtils();
    for (TypeMirror superInterface : type.getInterfaces()) {
      findLocalAndInheritedMethods((TypeElement) typeUtils.asElement(superInterface), methods);
    }
    if (type.getSuperclass().getKind() != TypeKind.NONE) {
      // Visit the superclass after superinterfaces so we will always see the implementation of a
      // method after any interfaces that declared it.
      findLocalAndInheritedMethods(
          (TypeElement) typeUtils.asElement(type.getSuperclass()), methods);
    }
    // Add each method of this class, and in so doing remove any inherited method it overrides.
    // This algorithm is quadratic in the number of methods but it's hard to see how to improve
    // that while still using Elements.overrides.
    List<ExecutableElement> theseMethods = ElementFilter.methodsIn(type.getEnclosedElements());
    for (ExecutableElement method : theseMethods) {
      if (!method.getModifiers().contains(Modifier.PRIVATE)) {
        boolean alreadySeen = false;
        for (Iterator<ExecutableElement> methodIter = methods.iterator(); methodIter.hasNext(); ) {
          ExecutableElement otherMethod = methodIter.next();
          if (elementUtils.overrides(method, otherMethod, type)) {
            methodIter.remove();
          } else if (method.getSimpleName().equals(otherMethod.getSimpleName())
              && method.getParameters().equals(otherMethod.getParameters())) {
            // If we inherit this method on more than one path, we don't want to add it twice.
            alreadySeen = true;
          }
        }
        if (!alreadySeen) {
          /*
          retroweibo.RetroWeibo.GET action = method.getAnnotation(retroweibo.RetroWeibo.GET.class);
          System.out.printf(
              "%s Action value = %s\n",
              method.getSimpleName(),
              action == null ? null : action.value() );
          */
          methods.add(method);
        }
      }
    }
  }

  private void processType(TypeElement type) {
    RetroWeibo autoValue = type.getAnnotation(RetroWeibo.class);
    if (autoValue == null) {
      // This shouldn't happen unless the compilation environment is buggy,
      // but it has happened in the past and can crash the compiler.
      errorReporter.abortWithError("annotation processor for @RetroWeibo was invoked with a type"
          + " that does not have that annotation; this is probably a compiler bug", type);
    }
    if (type.getKind() != ElementKind.CLASS) {
      errorReporter.abortWithError(
          "@" + RetroWeibo.class.getName() + " only applies to classes", type);
    }
    if (ancestorIsRetroWeibo(type)) {
      errorReporter.abortWithError("One @RetroWeibo class may not extend another", type);
    }
    if (implementsAnnotation(type)) {
      errorReporter.abortWithError("@RetroWeibo may not be used to implement an annotation"
          + " interface; try using @AutoAnnotation instead", type);
    }
    RetroWeiboTemplateVars vars = new RetroWeiboTemplateVars();
    vars.pkg = TypeSimplifier.packageNameOf(type);
    vars.origClass = TypeSimplifier.classNameOf(type);
    vars.simpleClassName = TypeSimplifier.simpleNameOf(vars.origClass);
    vars.subclass = TypeSimplifier.simpleNameOf(generatedSubclassName(type));
    defineVarsForType(type, vars);
    GwtCompatibility gwtCompatibility = new GwtCompatibility(type);
    vars.gwtCompatibleAnnotation = gwtCompatibility.gwtCompatibleAnnotationString();
    String text = vars.toText();
    text = Reformatter.fixup(text);
    writeSourceFile(generatedSubclassName(type), text, type);
    GwtSerialization gwtSerialization = new GwtSerialization(gwtCompatibility, processingEnv, type);
    gwtSerialization.maybeWriteGwtSerializer(vars);
  }

  private void defineVarsForType(TypeElement type, RetroWeiboTemplateVars vars) {
    Types typeUtils = processingEnv.getTypeUtils();
    List<ExecutableElement> methods = new ArrayList<ExecutableElement>();
    findLocalAndInheritedMethods(type, methods);
    determineObjectMethodsToGenerate(methods, vars);
    ImmutableSet<ExecutableElement> methodsToImplement = methodsToImplement(methods);
    Set<TypeMirror> types = new TypeMirrorSet();
    types.addAll(returnTypesOf(methodsToImplement));
    //    TypeMirror javaxAnnotationGenerated = getTypeMirror(Generated.class);
    //    types.add(javaxAnnotationGenerated);
    TypeMirror javaUtilArrays = getTypeMirror(Arrays.class);
    if (containsArrayType(types)) {
      // If there are array properties then we will be referencing java.util.Arrays.
      // Arrange to import it unless that would introduce ambiguity.
      types.add(javaUtilArrays);
    }
    BuilderSpec builderSpec = new BuilderSpec(type, processingEnv, errorReporter);
    Optional<BuilderSpec.Builder> builder = builderSpec.getBuilder();
    ImmutableSet<ExecutableElement> toBuilderMethods;
    if (builder.isPresent()) {
      types.add(getTypeMirror(BitSet.class));
      toBuilderMethods = builder.get().toBuilderMethods(typeUtils, methodsToImplement);
    } else {
      toBuilderMethods = ImmutableSet.of();
    }
    vars.toBuilderMethods =
        FluentIterable.from(toBuilderMethods).transform(SimpleNameFunction.INSTANCE).toList();
    Set<ExecutableElement> propertyMethods = Sets.difference(methodsToImplement, toBuilderMethods);
    String pkg = TypeSimplifier.packageNameOf(type);
    TypeSimplifier typeSimplifier = new TypeSimplifier(typeUtils, pkg, types, type.asType());
    vars.imports = typeSimplifier.typesToImport();
    //    vars.generated = typeSimplifier.simplify(javaxAnnotationGenerated);
    vars.arrays = typeSimplifier.simplify(javaUtilArrays);
    vars.bitSet = typeSimplifier.simplifyRaw(getTypeMirror(BitSet.class));
    ImmutableMap<ExecutableElement, String> methodToPropertyName =
        methodToPropertyNameMap(propertyMethods);
    Map<ExecutableElement, String> methodToIdentifier =
        Maps.newLinkedHashMap(methodToPropertyName);
    fixReservedIdentifiers(methodToIdentifier);
    List<Property> props = new ArrayList<Property>();
    for (ExecutableElement method : propertyMethods) {
      String propertyType = typeSimplifier.simplify(method.getReturnType());
      String propertyName = methodToPropertyName.get(method);
      String identifier = methodToIdentifier.get(method);
      List<String> args = new ArrayList<String>();
      props.add(new Property(propertyName, identifier, method, propertyType, typeSimplifier, processingEnv));
    }
    // If we are running from Eclipse, undo the work of its compiler which sorts methods.
    eclipseHack().reorderProperties(props);
    vars.props = props;
    vars.serialVersionUID = getSerialVersionUID(type);
    vars.formalTypes = typeSimplifier.formalTypeParametersString(type);
    vars.actualTypes = TypeSimplifier.actualTypeParametersString(type);
    vars.wildcardTypes = wildcardTypeParametersString(type);

    TypeElement parcelable = processingEnv.getElementUtils().getTypeElement("android.os.Parcelable");
    vars.parcelable = parcelable != null
        && processingEnv.getTypeUtils().isAssignable(type.asType(), parcelable.asType());
    // Check for @RetroWeibo.Builder and add appropriate variables if it is present.
    if (builder.isPresent()) {
      builder.get().defineVars(vars, typeSimplifier, methodToPropertyName);
    }
  }

  private ImmutableMap<ExecutableElement, String> methodToPropertyNameMap(
      Iterable<ExecutableElement> propertyMethods) {
    ImmutableMap.Builder<ExecutableElement, String> builder = ImmutableMap.builder();
    boolean allGetters = allGetters(propertyMethods);
    for (ExecutableElement method : propertyMethods) {
      String methodName = method.getSimpleName().toString();
      String name = allGetters ? nameWithoutPrefix(methodName) : methodName;
      builder.put(method, name);
    }
    ImmutableMap<ExecutableElement, String> map = builder.build();
    if (allGetters) {
      checkDuplicateGetters(map);
    }
    return map;
  }

  private static boolean allGetters(Iterable<ExecutableElement> methods) {
    if (true) return true;
    for (ExecutableElement method : methods) {
      String name = method.getSimpleName().toString();
      // TODO(user): decide whether getfoo() (without a capital) is a getter. Currently it is.
      boolean get = name.startsWith("get") && !name.equals("get");
      boolean is = name.startsWith("is") && !name.equals("is")
          && method.getReturnType().getKind() == TypeKind.BOOLEAN;
      if (!get && !is) {
        return false;
      }
    }
    return true;
  }

  private String nameWithoutPrefix(String name) {
    if (name.startsWith("get")) {
      name = name.substring(3);
    } else {
      assert name.startsWith("is");
      name = name.substring(2);
    }
    return Introspector.decapitalize(name);
  }

  private void checkDuplicateGetters(Map<ExecutableElement, String> methodToIdentifier) {
      if (true) return;
    Set<String> seen = Sets.newHashSet();
    for (Map.Entry<ExecutableElement, String> entry : methodToIdentifier.entrySet()) {
      if (!seen.add(entry.getValue())) {
        errorReporter.reportError(
            "More than one @RetroWeibo property called " + entry.getValue(), entry.getKey());
      }
    }
  }

  // If we have a getter called getPackage() then we can't use the identifier "package" to represent
  // its value since that's a reserved word.
  private void fixReservedIdentifiers(Map<ExecutableElement, String> methodToIdentifier) {
    for (Map.Entry<ExecutableElement, String> entry : methodToIdentifier.entrySet()) {
      if (SourceVersion.isKeyword(entry.getValue())) {
        entry.setValue(disambiguate(entry.getValue(), methodToIdentifier.values()));
      }
    }
  }

  private String disambiguate(String name, Collection<String> existingNames) {
    for (int i = 0; ; i++) {
      String candidate = name + i;
      if (!existingNames.contains(candidate)) {
        return candidate;
      }
    }
  }

  private Set<TypeMirror> returnTypesOf(Iterable<ExecutableElement> methods) {
    Set<TypeMirror> returnTypes = new TypeMirrorSet();
    for (ExecutableElement method : methods) {
      returnTypes.add(method.getReturnType());
    }
    return returnTypes;
  }

  private static boolean containsArrayType(Set<TypeMirror> types) {
    for (TypeMirror type : types) {
      if (type.getKind() == TypeKind.ARRAY) {
        return true;
      }
    }
    return false;
  }

  /**
   * Given a list of all methods defined in or inherited by a class, sets the equals, hashCode, and
   * toString fields of vars according as the corresponding methods should be generated.
   */
  private static void determineObjectMethodsToGenerate(
      List<ExecutableElement> methods, RetroWeiboTemplateVars vars) {
    // The defaults here only come into play when an ancestor class doesn't exist.
    // Compilation will fail in that case, but we don't want it to crash the compiler with
    // an exception before it does. If all ancestors do exist then we will definitely find
    // definitions of these three methods (perhaps the ones in Object) so we will overwrite these:
    vars.equals = false;
    vars.hashCode = false;
    vars.toString = false;
    for (ExecutableElement method : methods) {
      ObjectMethodToOverride override = objectMethodToOverride(method);
      boolean canGenerate = method.getModifiers().contains(Modifier.ABSTRACT)
          || isJavaLangObject((TypeElement) method.getEnclosingElement());
      switch (override) {
        case EQUALS:
          vars.equals = canGenerate;
          break;
        case HASH_CODE:
          vars.hashCode = canGenerate;
          break;
        case TO_STRING:
          vars.toString = canGenerate;
          break;
      }
    }
  }

  private ImmutableSet<ExecutableElement> methodsToImplement(List<ExecutableElement> methods) {
    ImmutableSet.Builder<ExecutableElement> toImplement = ImmutableSet.builder();
    boolean errors = false;
    for (ExecutableElement method : methods) {
      if (method.getModifiers().contains(Modifier.ABSTRACT)
          && objectMethodToOverride(method) == ObjectMethodToOverride.NONE) {
        if (method.getParameters().isEmpty() && method.getReturnType().getKind() != TypeKind.VOID) {
          if (isReferenceArrayType(method.getReturnType())) {
            errorReporter.reportError("An @RetroWeibo class cannot define an array-valued property"
                + " unless it is a primitive array", method);
            errors = true;
          }
          toImplement.add(method);
        } else {
          toImplement.add(method);
        }
      }
    }
    if (errors) {
      throw new AbortProcessingException();
    }
    return toImplement.build();
  }

  private static boolean isReferenceArrayType(TypeMirror type) {
    return type.getKind() == TypeKind.ARRAY
        && !((ArrayType) type).getComponentType().getKind().isPrimitive();
  }

  private void writeSourceFile(String className, String text, TypeElement originatingType) {
    try {
      JavaFileObject sourceFile =
          processingEnv.getFiler().createSourceFile(className, originatingType);
      Writer writer = sourceFile.openWriter();
      try {
        writer.write(text);
      } finally {
        writer.close();
      }
    } catch (IOException e) {
      processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
          "Could not write generated class " + className + ": " + e);
    }
  }

  private boolean ancestorIsRetroWeibo(TypeElement type) {
    while (true) {
      TypeMirror parentMirror = type.getSuperclass();
      if (parentMirror.getKind() == TypeKind.NONE) {
        return false;
      }
      Types typeUtils = processingEnv.getTypeUtils();
      TypeElement parentElement = (TypeElement) typeUtils.asElement(parentMirror);
      if (parentElement.getAnnotation(RetroWeibo.class) != null) {
        return true;
      }
      type = parentElement;
    }
  }

  private boolean implementsAnnotation(TypeElement type) {
    Types typeUtils = processingEnv.getTypeUtils();
    return typeUtils.isAssignable(type.asType(), getTypeMirror(Annotation.class));
  }

  // Return a string like "1234L" if type instanceof Serializable and defines
  // serialVersionUID = 1234L, otherwise "".
  private String getSerialVersionUID(TypeElement type) {
    Types typeUtils = processingEnv.getTypeUtils();
    TypeMirror serializable = getTypeMirror(Serializable.class);
    if (typeUtils.isAssignable(type.asType(), serializable)) {
      List<VariableElement> fields = ElementFilter.fieldsIn(type.getEnclosedElements());
      for (VariableElement field : fields) {
        if (field.getSimpleName().toString().equals("serialVersionUID")) {
          Object value = field.getConstantValue();
          if (field.getModifiers().containsAll(Arrays.asList(Modifier.STATIC, Modifier.FINAL))
              && field.asType().getKind() == TypeKind.LONG
              && value != null) {
            return value + "L";
          } else {
            errorReporter.reportError(
                "serialVersionUID must be a static final long compile-time constant", field);
            break;
          }
        }
      }
    }
    return "";
  }

  private TypeMirror getTypeMirror(Class<?> c) {
    return getTypeMirror(processingEnv, c);
  }

  private static TypeMirror getTypeMirror(ProcessingEnvironment processingEnv, Class<?> c) {
    return processingEnv.getElementUtils().getTypeElement(c.getCanonicalName()).asType();
  }

  // The @RetroWeibo type, with a ? for every type.
  // If we have @RetroWeibo abstract class Foo<T extends Something> then this method will return
  // just <?>.
  private static String wildcardTypeParametersString(TypeElement type) {
    List<? extends TypeParameterElement> typeParameters = type.getTypeParameters();
    if (typeParameters.isEmpty()) {
      return "";
    } else {
      return "<"
          + Joiner.on(", ").join(
          FluentIterable.from(typeParameters).transform(Functions.constant("?")))
          + ">";
    }
  }

  private static String catArgsString(ExecutableElement method) {
    List<? extends VariableElement> parameters = method.getParameters();
    if (parameters.isEmpty()) {
      return "";
    } else {
      return ""
        + Joiner.on(" + ").join(
        FluentIterable.from(parameters).transform(new Function<VariableElement, String>() {
          @Override
          public String apply(VariableElement element) {
            return "" + element.getSimpleName();
          }
        }))
        + "";
    }
  }

  private static String formalArgsString(ExecutableElement method) {
    List<? extends VariableElement> parameters = method.getParameters();
    if (parameters.isEmpty()) {
      return "";
    } else {
      return ""
        + Joiner.on(", ").join(
        FluentIterable.from(parameters).transform(new Function<VariableElement, String>() {
          @Override
          public String apply(VariableElement element) {
            return "" + element.getSimpleName();
          }
        }))
        + "";
    }
  }

  private static String formalTypeArgsString(ExecutableElement method) {
    List<? extends VariableElement> parameters = method.getParameters();
    if (parameters.isEmpty()) {
      return "";
    } else {
      return ""
        + Joiner.on(", ").join(
        FluentIterable.from(parameters).transform(new Function<VariableElement, String>() {
          @Override
          public String apply(VariableElement element) {
            return "final " + element.asType() + " " + element.getSimpleName();
          }
        }))
        + "";
    }
  }

  private EclipseHack eclipseHack() {
    return new EclipseHack(processingEnv);
  }
}
