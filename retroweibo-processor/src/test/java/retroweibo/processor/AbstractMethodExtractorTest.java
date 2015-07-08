package retroweibo.processor;

import com.google.common.collect.ImmutableMultimap;

import junit.framework.TestCase;

import java.io.StringReader;

/**
 * Tests for {@link AbstractMethodExtractor}.
 *
 * @author Éamonn McManus
 */
public class AbstractMethodExtractorTest extends TestCase {
  public void testSimple() {
    String source = "package com.example;\n"
        + "import retroweibo.RetroWeibo;\n"
        + "import java.util.Map;\n"
        + "@RetroWeibo"
        + "abstract class Foo {\n"
        + "  Foo(int one, String two, Map<String, String> three) {\n"
        + "    return new RetroWeibo_Foo(one, two, three);\n"
        + "  }\n"
        + "  abstract int one();\n"
        + "  abstract String two();\n"
        + "  abstract Map<String, String> three();\n"
        + "}\n";
    JavaTokenizer tokenizer = new JavaTokenizer(new StringReader(source));
    AbstractMethodExtractor extractor = new AbstractMethodExtractor();
    ImmutableMultimap<String, String> expected = ImmutableMultimap.of(
        "com.example.Foo", "one",
        "com.example.Foo", "two",
        "com.example.Foo", "three");
    ImmutableMultimap<String, String> actual = extractor.abstractMethods(tokenizer, "com.example");
    assertEquals(expected, actual);
  }

  public void testNested() {
    String source = "package com.example;\n"
        + "import retroweibo.RetroWeibo;\n"
        + "import java.util.Map;\n"
        + "abstract class Foo {\n"
        + "  @RetroWeibo\n"
        + "  abstract class Baz {\n"
        + "    abstract <T extends Number & Comparable<T>> T complicated();\n"
        + "    abstract int simple();\n"
        + "    abstract class Irrelevant {\n"
        + "      void distraction() {\n"
        + "        abstract class FurtherDistraction {\n"
        + "          abstract int buh();\n"
        + "        }\n"
        + "      }\n"
        + "    }\n"
        + "  }\n"
        + "  @RetroWeibo\n"
        + "  abstract class Bar {\n"
        + "    abstract String whatever();\n"
        + "  }\n"
        + "  abstract class AlsoIrrelevant {\n"
        + "    void distraction() {}\n"
        + "  }\n"
        + "}\n";
    JavaTokenizer tokenizer = new JavaTokenizer(new StringReader(source));
    AbstractMethodExtractor extractor = new AbstractMethodExtractor();
    ImmutableMultimap<String, String> expected = ImmutableMultimap.of(
        "com.example.Foo.Baz", "complicated",
        "com.example.Foo.Baz", "simple",
        "com.example.Foo.Bar", "whatever");
    ImmutableMultimap<String, String> actual = extractor.abstractMethods(tokenizer, "com.example");
    assertEquals(expected, actual);
  }

  public void testClassConstants() {
    // Regression test for a bug where String.class was parsed as introducing a class definition
    // of a later identifier.
    String source = "package com.example;\n"
        + "import retroweibo.RetroWeibo;\n"
        + "import com.google.common.collect.ImmutableSet;\n"
        + "import com.google.common.labs.reflect.ValueType;\n"
        + "import com.google.common.primitives.Primitives;\n"
        + "public final class ProducerMetadata<T> extends ValueType {\n"
        + "  private static final ImmutableSet<Class<?>> ALLOWABLE_MAP_KEY_TYPES =\n"
        + "    ImmutableSet.<Class<?>>builder()\n"
        + "    .addAll(Primitives.allPrimitiveTypes())\n"
        + "    .addAll(Primitives.allWrapperTypes())\n"
        + "    .add(String.class)\n"
        + "    .add(Class.class)\n"
        + "    .build();\n"
        + "  @RetroWeibo abstract static class SourcedKeySet {\n"
        + "    abstract ImmutableSet<Key<?>> unknownSource();\n"
        + "    abstract ImmutableSet<Key<?>> fromInputs();\n"
        + "    abstract ImmutableSet<Key<?>> fromNodes();\n"
        + "    abstract ImmutableSet<Key<?>> all();\n"
        + "  }\n"
        + "}";
    JavaTokenizer tokenizer = new JavaTokenizer(new StringReader(source));
    AbstractMethodExtractor extractor = new AbstractMethodExtractor();
    ImmutableMultimap<String, String> expected = ImmutableMultimap.of(
        "com.example.ProducerMetadata.SourcedKeySet", "unknownSource",
        "com.example.ProducerMetadata.SourcedKeySet", "fromInputs",
        "com.example.ProducerMetadata.SourcedKeySet", "fromNodes",
        "com.example.ProducerMetadata.SourcedKeySet", "all");
    ImmutableMultimap<String, String> actual = extractor.abstractMethods(tokenizer, "com.example");
    assertEquals(expected, actual);
  }
}
