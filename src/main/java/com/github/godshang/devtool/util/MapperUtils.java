package com.github.godshang.devtool.util;

import com.deblock.jsondiff.DiffGenerator;
import com.deblock.jsondiff.matcher.CompositeJsonMatcher;
import com.deblock.jsondiff.matcher.LenientNumberPrimitivePartialMatcher;
import com.deblock.jsondiff.matcher.StrictJsonArrayPartialMatcher;
import com.deblock.jsondiff.matcher.StrictJsonObjectPartialMatcher;
import com.deblock.jsondiff.matcher.StrictPrimitivePartialMatcher;
import com.deblock.jsondiff.viewer.PatchDiffViewer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.github.godshang.devtool.common.Result;
import lombok.SneakyThrows;

public class MapperUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ObjectMapper prettyObjectMapper = new ObjectMapper();
    private static final DefaultPrettyPrinter prettyPrinter;
    private static final YAMLMapper yamlMapper = new YAMLMapper();
    private static final JavaPropsMapper javaPropsMapper = new JavaPropsMapper();

    static {
        prettyObjectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        DefaultPrettyPrinter.Indenter indenter =
                new DefaultIndenter("    ", DefaultIndenter.SYS_LF);
        prettyPrinter = new DefaultPrettyPrinter();
        prettyPrinter.indentObjectsWith(indenter);
        prettyPrinter.indentArraysWith(indenter);
    }

    public static String toString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @SneakyThrows
    private static String prettyJson(JsonNode jsonNode) {
        return prettyObjectMapper.writer(prettyPrinter).writeValueAsString(jsonNode);
    }

    public static boolean isValidJson(String json) {
        try {
            readTree(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Result<String> prettyPrint(String json) {
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            String prettyJson = prettyJson(jsonNode);
            return Result.createSuccess(prettyJson);
        } catch (JsonProcessingException e) {
            String errorMsg = e.getOriginalMessage();
            return Result.createFail(errorMsg);
        }
    }

    public static String escapeJson(String raw) {
        String escaped = raw;
        escaped = escaped.replace("\\", "\\\\");
        escaped = escaped.replace("\"", "\\\"");
//        escaped = escaped.replace("\b", "\\b");
//        escaped = escaped.replace("\f", "\\f");
//        escaped = escaped.replace("\n", "\\n");
//        escaped = escaped.replace("\r", "\\r");
//        escaped = escaped.replace("\t", "\\t");
        // TODO: escape other non-printing characters using uXXXX notation
        return escaped;
    }

    public static String unEscapeJson(String raw) {
        String escaped = raw;
        escaped = escaped.replace("\\\\", "\\");
        escaped = escaped.replace("\\\"", "\"");
//        escaped = escaped.replace("\\b", "\b");
//        escaped = escaped.replace("\\f", "\f");
//        escaped = escaped.replace("\\n", "\n");
//        escaped = escaped.replace("\\r", "\r");
//        escaped = escaped.replace("\\t", "\t");
        // TODO: escape other non-printing characters using uXXXX notation
        return escaped;
    }

    public static JsonNode readTree(String json) {
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static Result<String> jsonToYaml(String json) {
        try {
            String yaml = yamlMapper.writeValueAsString(objectMapper.readTree(json));
            return Result.createSuccess(yaml);
        } catch (JsonProcessingException e) {
            String errorMsg = e.getOriginalMessage();
            return Result.createFail(errorMsg);
        }
    }

    public static Result<String> yamlToJson(String yaml) {
        try {
            JsonNode jsonNode = yamlMapper.readTree(yaml);
            String json = prettyJson(jsonNode);
            return Result.createSuccess(json);
        } catch (JsonProcessingException e) {
            String errorMsg = e.getOriginalMessage();
            return Result.createFail(errorMsg);
        }
    }

    public static Result<String> jsonToProps(String json) {
        try {
            String props = javaPropsMapper.writeValueAsString(objectMapper.readTree(json));
            return Result.createSuccess(props);
        } catch (JsonProcessingException e) {
            String errorMsg = e.getOriginalMessage();
            return Result.createFail(errorMsg);
        }
    }

    public static Result<String> propsToJson(String props) {
        try {
            JsonNode jsonNode = javaPropsMapper.readTree(props);
            String json = prettyJson(jsonNode);
            return Result.createSuccess(json);
        } catch (JsonProcessingException e) {
            String errorMsg = e.getOriginalMessage();
            return Result.createFail(errorMsg);
        }
    }

    public static Result<String> propsToYaml(String props) {
        try {
            String yaml = yamlMapper.writeValueAsString(javaPropsMapper.readTree(props));
            return Result.createSuccess(yaml);
        } catch (JsonProcessingException e) {
            String errorMsg = e.getOriginalMessage();
            return Result.createFail(errorMsg);
        }
    }

    public static Result<String> yamlToProps(String props) {
        try {
            String yaml = javaPropsMapper.writeValueAsString(yamlMapper.readTree(props));
            return Result.createSuccess(yaml);
        } catch (JsonProcessingException e) {
            String errorMsg = e.getOriginalMessage();
            return Result.createFail(errorMsg);
        }
    }

    /**
     * https://github.com/deblockt/json-diff
     */
    public static String diffJson(String expected, String actual) {
        // define your matcher
        // CompositeJsonMatcher use other matcher to perform matching on objects, list or primitive
        var jsonMatcher = new CompositeJsonMatcher(
                new StrictJsonArrayPartialMatcher(), // comparing array using strict mode (object should have same properties/value)
                new StrictJsonObjectPartialMatcher(), // comparing object using strict mode (array should have same item on same orders)
                new LenientNumberPrimitivePartialMatcher(new StrictPrimitivePartialMatcher()) // comparing primitive types and manage numbers (100.00 == 100)
        );
        // generate a diff
        var jsondiff = DiffGenerator.diff(expected, actual, jsonMatcher);
        // use the viewer to collect diff data
        var patch = PatchDiffViewer.from(jsondiff);
        return patch.toString();
    }
}
