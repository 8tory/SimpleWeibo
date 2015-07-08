/*
 * Copyright (C) 2014 Google, Inc.
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

import com.google.common.base.Optional;

import org.apache.velocity.runtime.parser.node.SimpleNode;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.zip.CRC32;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * Generates GWT serialization code for {@code @RetroWeibo} classes also marked
 * {@code @GwtCompatible(serializable = true)}.
 *
 * @author Éamonn McManus
 */
class GwtSerialization {
  private final GwtCompatibility gwtCompatibility;
  private final ProcessingEnvironment processingEnv;
  private final TypeElement type;

  GwtSerialization(
      GwtCompatibility gwtCompatibility, ProcessingEnvironment processingEnv, TypeElement type) {
    this.gwtCompatibility = gwtCompatibility;
    this.processingEnv = processingEnv;
    this.type = type;
  }

  private boolean shouldWriteGwtSerializer() {
    Optional<AnnotationMirror> optionalGwtCompatible = gwtCompatibility.gwtCompatibleAnnotation();
    if (optionalGwtCompatible.isPresent()) {
      AnnotationMirror gwtCompatible = optionalGwtCompatible.get();
      for (Map.Entry<ExecutableElement, AnnotationValue> entry :
          Collections.unmodifiableMap(gwtCompatible.getElementValues()).entrySet()) {
        if (entry.getKey().getSimpleName().contentEquals("serializable")
            && entry.getValue().getValue().equals(true)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Writes the GWT serializer for the given type, if appropriate. An {@code @RetroWeibo} class gets
   * a GWT serializer if it is annotated with {@code @GwtCompatible(serializable = true)}, where the
   * {@code @GwtCompatible} annotation can come from any package.
   *
   * <p>If the type is com.example.Foo then the generated RetroWeibo subclass is
   * com.example.RetroWeibo_Foo and the GWT serializer is
   * com.example.RetroWeibo_Foo_CustomFieldSerializer.
   *
   * @param autoVars the template variables defined for this type.
   */
  void maybeWriteGwtSerializer(RetroWeiboTemplateVars autoVars) {
    if (shouldWriteGwtSerializer()) {
      GwtTemplateVars vars = new GwtTemplateVars();
      vars.imports = autoVars.imports;
      vars.pkg = autoVars.pkg;
      vars.subclass = autoVars.subclass;
      String className = (vars.pkg.isEmpty() ? "" : vars.pkg + ".") + vars.subclass
          + "_CustomFieldSerializer";
      vars.serializerClass = TypeSimplifier.simpleNameOf(className);
      vars.props = new ArrayList<Property>();
      for (RetroWeiboProcessor.Property prop : autoVars.props) {
        vars.props.add(new Property(prop));
      }
      vars.classHashString = computeClassHash(autoVars.props);
      String text = vars.toText();
      writeSourceFile(className, text, type);
    }
  }

  public static class Property {
    private final RetroWeiboProcessor.Property property;
    private final boolean isCastingUnchecked;

    Property(RetroWeiboProcessor.Property property) {
      this.property = property;
      this.isCastingUnchecked = TypeSimplifier.isCastingUnchecked(property.getReturnType());
    }

    @Override public String toString() {
      return property.toString();
    }

    public String getGetter() {
      return property.getGetter();
    }

    public String getType() {
      return property.getType();
    }

    /**
     * Returns the suffix in serializer method names for values of the given type. For example,
     * if the type is "int" then the returned value will be "Int" because the serializer methods
     * are called readInt and writeInt. There are methods for all primitive types and String;
     * every other type uses readObject and writeObject.
     */
    public String getGwtType() {
      String type = property.getType();
      if (property.getKind().isPrimitive()) {
        return Character.toUpperCase(type.charAt(0)) + type.substring(1);
      } else if (type.equals("String")) {
        return "String";
      } else {
        return "Object";
      }
    }

    /**
     * Returns a string to be inserted before the call to the readFoo() call so that the expression
     * can be assigned to the given type. For primitive types and String, the readInt() etc methods
     * already return the right type so the string is empty. For other types, the string is a cast
     * like "(Foo) ".
     */
    public String getGwtCast() {
      if (property.getKind().isPrimitive() || getType().equals("String")) {
        return "";
      } else {
        return "(" + getType() + ") ";
      }
    }

    public boolean isCastingUnchecked() {
      return isCastingUnchecked;
    }
  }

  @SuppressWarnings("unused")  // some fields are only read through reflection
  static class GwtTemplateVars extends TemplateVars {
    /** The properties defined by the parent class's abstract methods. */
    List<Property> props;

    /** The fully-qualified names of the classes to be imported in the generated class. */
    SortedSet<String> imports;

    /**
     * The package of the class with the {@code @RetroWeibo} annotation and its generated subclass.
     */
    String pkg;

    /** The simple name of the generated subclass. */
    String subclass;

    /** The simple name of the generated GWT serializer class. */
    String serializerClass;

    /** A string that should change if any salient details of the serialized class change. */
    String classHashString;

    private static final SimpleNode TEMPLATE = parsedTemplateForResource("gwtserializer.vm");

    @Override
    SimpleNode parsedTemplate() {
      return TEMPLATE;
    }
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

  private static final Charset UTF8 = Charset.forName("UTF-8");

  private String computeClassHash(List<RetroWeiboProcessor.Property> props) {
    TypeSimplifier typeSimplifier = new TypeSimplifier(
        processingEnv.getTypeUtils(), "", new TypeMirrorSet(), null);
    CRC32 crc = new CRC32();
    update(crc, typeSimplifier.simplify(type.asType()) + ":");
    for (RetroWeiboProcessor.Property prop : props) {
      update(crc, prop.toString() + ":" + prop.getType() + ";");
    }
    return String.format("%08x", crc.getValue());
  }

  private static void update(CRC32 crc, String s) {
    crc.update(s.getBytes(UTF8));
  }
}
