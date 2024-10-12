package com.github.godshang.devtool.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


public class Json2BeanUtils {

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option {
        private String packageName;
        private String className;
        private boolean useLombok;

        public String getClassName() {
            return className == null || className.isBlank() ? "Root" : className;
        }

        public String getPackageName() {
            return packageName == null || packageName.isBlank() ? "" : packageName;
        }

        public boolean isUseLombok() {
            return useLombok;
        }
    }

    public static String generate(String json) {
        return generate(new Option(), json);
    }

    public static String generate(Option option, String json) {
        JsonNode root = MapperUtils.readTree(json);
        if (root == null) {
            return "Incorrect JSON";
        }
        return generate(option, root);
    }

    public static String generate(Option option, JsonNode root) {
        if (!root.isObject()) {
            return "Array Not Supported";
        }
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(option.getClassName())
                .addModifiers(Modifier.PUBLIC);
        if (option.isUseLombok()) {
            typeSpecBuilder.addAnnotation(ClassName.get("lombok", "Data"));
        }
        doGenerate(option, root, typeSpecBuilder, typeSpecBuilder);
        JavaFile javaFile = JavaFile.builder(option.getPackageName(), typeSpecBuilder.build())
                .build();
        return javaFile.toString();
    }

    public static void doGenerate(Option option, JsonNode jsonNode, TypeSpec.Builder rootTypeSpec, TypeSpec.Builder parentTypeSpec) {
        Iterator<Map.Entry<String, JsonNode>> iter = jsonNode.fields();
        while (iter.hasNext()) {
            Map.Entry<String, JsonNode> entry = iter.next();
            String fieldName = entry.getKey();
            JsonNode fieldValue = entry.getValue();

            if (fieldValue.isArray()) {
                Iterator<JsonNode> _iter = fieldValue.iterator();
                if (_iter.hasNext()) {
                    JsonNode firstNode = _iter.next();
                    if (firstNode.isObject()) {
                        String className = getClassName(fieldName);
                        // add Class
                        if (!rootTypeSpec.typeSpecs.stream().map(e -> e.name).collect(Collectors.toSet()).contains(className)) {
                            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                            if (option.isUseLombok()) {
                                typeSpecBuilder.addAnnotation(ClassName.get("lombok", "Data"));
                            }

                            doGenerate(option, firstNode, rootTypeSpec, typeSpecBuilder);
                            rootTypeSpec.addType(typeSpecBuilder.build());
                        }

                        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(ClassName.get(List.class),
                                ClassName.get(option.getPackageName(), className));
                        addField(parentTypeSpec, parameterizedTypeName, fieldName);
                        if (!option.isUseLombok()) {
                            addSetter(parentTypeSpec, parameterizedTypeName, fieldName);
                            addGetter(parentTypeSpec, parameterizedTypeName, fieldName);
                        }
                    } else if (!firstNode.isArray()) { // ignore nested array
                        Type type = getType(firstNode);
                        ParameterizedTypeName emptyListTypeName = ParameterizedTypeName.get(List.class, type);
                        addField(parentTypeSpec, emptyListTypeName, fieldName);
                        if (!option.isUseLombok()) {
                            addSetter(parentTypeSpec, emptyListTypeName, fieldName);
                            addGetter(parentTypeSpec, emptyListTypeName, fieldName);
                        }
                    }
                } else {
                    ParameterizedTypeName emptyListTypeName = ParameterizedTypeName.get(List.class, Object.class);
                    addField(parentTypeSpec, emptyListTypeName, fieldName);
                    if (!option.isUseLombok()) {
                        addSetter(parentTypeSpec, emptyListTypeName, fieldName);
                        addGetter(parentTypeSpec, emptyListTypeName, fieldName);
                    }
                }
            } else if (fieldValue.isObject()) {
                String className = getClassName(fieldName);
                // add Class
                if (!rootTypeSpec.typeSpecs.stream().map(e -> e.name).collect(Collectors.toSet()).contains(className)) {
                    TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
                    if (option.isUseLombok()) {
                        typeSpecBuilder.addAnnotation(ClassName.get("lombok", "Data"));
                    }
                    doGenerate(option, fieldValue, rootTypeSpec, typeSpecBuilder);
                    rootTypeSpec.addType(typeSpecBuilder.build());
                }

                TypeName typeName = ClassName.get(option.getPackageName(), className);
                addField(parentTypeSpec, typeName, fieldName);
                if (!option.isUseLombok()) {
                    addSetter(parentTypeSpec, typeName, fieldName);
                    addGetter(parentTypeSpec, typeName, fieldName);
                }
            } else {
                Type type = getType(fieldValue);
                addField(parentTypeSpec, type, fieldName);
                if (!option.isUseLombok()) {
                    addSetter(parentTypeSpec, type, fieldName);
                    addGetter(parentTypeSpec, type, fieldName);
                }
            }
        }
    }

    private static Type getType(JsonNode jsonNode) {
        Type type = Object.class;
        if (jsonNode.isTextual()) {
            type = String.class;
        } else if (jsonNode.isBigDecimal()) {
            type = BigDecimal.class;
        } else if (jsonNode.isBigInteger()) {
            type = BigInteger.class;
        } else if (jsonNode.isBoolean()) {
            type = Boolean.class;
        } else if (jsonNode.isDouble()) {
            type = Double.class;
        } else if (jsonNode.isFloat()) {
            type = Float.class;
        } else if (jsonNode.isInt()) {
            type = Integer.class;
        } else if (jsonNode.isLong()) {
            type = Long.class;
        } else if (jsonNode.isShort()) {
            type = Short.class;
        } else if (jsonNode.isNumber()) {
            type = Number.class;
        }
        return type;
    }

    private static String getClassName(String fieldName) {
        return StringUtils.capitalize(fieldName);
    }

    private static void addField(TypeSpec.Builder typeSpecBuilder, Type type, String fieldName) {
        typeSpecBuilder.addField(FieldSpec.builder(type, fieldName)
                .addModifiers(Modifier.PRIVATE)
                .build());
    }

    private static void addField(TypeSpec.Builder typeSpecBuilder, TypeName typeName, String fieldName) {
        typeSpecBuilder.addField(FieldSpec.builder(typeName, fieldName)
                .addModifiers(Modifier.PRIVATE)
                .build());
    }

    private static void addSetter(TypeSpec.Builder typeSpecBuilder, Type type, String fieldName) {
        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("set" + StringUtils.capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(type, fieldName)
                .addCode("this." + fieldName + " = " + fieldName + ";")
                .build());
    }

    private static void addSetter(TypeSpec.Builder typeSpecBuilder, TypeName typeName, String fieldName) {
        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("set" + StringUtils.capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(typeName, fieldName)
                .addCode("this." + fieldName + " = " + fieldName + ";")
                .build());
    }

    private static void addGetter(TypeSpec.Builder typeSpecBuilder, Type type, String fieldName) {
        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("get" + StringUtils.capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(type)
                .addCode("return this." + fieldName + ";")
                .build());
    }

    private static void addGetter(TypeSpec.Builder typeSpecBuilder, TypeName typeName, String fieldName) {
        typeSpecBuilder.addMethod(MethodSpec.methodBuilder("get" + StringUtils.capitalize(fieldName))
                .addModifiers(Modifier.PUBLIC)
                .returns(typeName)
                .addCode("return this." + fieldName + ";")
                .build());
    }

    public static void main(String[] args) {
        String json = "{\"result\":{\"code\":\"C200\",\"message\":\"成功\"},\"yesterday\":{\"totalShareAmount\":182449.734,\"data\":[{\"businessTag\":4,\"businessTagName\":\"儿童\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":0,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":0,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":0,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":5,\"businessTagName\":\"综艺\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":0,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":0,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":0,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":11,\"businessTagName\":\"竖短片/短剧\",\"totalShareAmount\":63979.274,\"projectShareAmount\":63979.274,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":4561.25,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":300,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":51246.08,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":7871.944,\"show\":true}]},{\"businessTag\":6,\"businessTagName\":\"纪录片\",\"totalShareAmount\":43879.27,\"projectShareAmount\":43879.27,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":4561.25,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":200,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":34032.68,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":5085.34,\"show\":true}]},{\"businessTag\":12,\"businessTagName\":\"游戏短视频\",\"totalShareAmount\":43879.27,\"projectShareAmount\":43879.27,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":4561.25,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":200,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":34032.68,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":5085.34,\"show\":true}]},{\"businessTag\":1,\"businessTagName\":\"电视剧\",\"totalShareAmount\":0.0,\"projectShareAmount\":0.0,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":0,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":0,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":0,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":0,\"show\":true},{\"incomeType\":5,\"incomeTypeName\":\"海外发行收入\",\"incomeTypeEnum\":\"OVERSEA_ISSUE\",\"shareAmount\":0.0,\"show\":true}]},{\"businessTag\":2,\"businessTagName\":\"电影\",\"totalShareAmount\":0.0,\"projectShareAmount\":0.0,\"incomeList\":[{\"incomeType\":6,\"incomeTypeName\":\"单点收入\",\"incomeTypeEnum\":\"DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":0,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":0,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":0,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":0,\"show\":true},{\"incomeType\":5,\"incomeTypeName\":\"海外发行收入\",\"incomeTypeEnum\":\"OVERSEA_ISSUE\",\"shareAmount\":0.0,\"show\":true}]},{\"businessTag\":3,\"businessTagName\":\"动漫\",\"totalShareAmount\":30701.19,\"projectShareAmount\":30701.19,\"incomeList\":[{\"incomeType\":6,\"incomeTypeName\":\"单点收入\",\"incomeTypeEnum\":\"DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":0,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":0,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":30199.24,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":501.95,\"show\":true}]},{\"businessTag\":8,\"businessTagName\":\"UID制(视频广告)\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":7,\"incomeTypeName\":\"信息流广告收入\",\"incomeTypeEnum\":\"FEED\",\"shareAmount\":0,\"show\":true},{\"incomeType\":9,\"incomeTypeName\":\"小视频广告收入\",\"incomeTypeEnum\":\"SMALL_VIDEO\",\"shareAmount\":0,\"show\":true},{\"incomeType\":8,\"incomeTypeName\":\"贴片广告收入\",\"incomeTypeEnum\":\"PASTE\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":60,\"businessTagName\":\"文学\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":12,\"incomeTypeName\":\"文学非稿费\",\"incomeTypeEnum\":\"BOOK_UN_PAYMENT\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":70,\"businessTagName\":\"漫画\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":14,\"incomeTypeName\":\"漫画单订\",\"incomeTypeEnum\":\"CARTOON_DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":13,\"incomeTypeName\":\"漫画稿费\",\"incomeTypeEnum\":\"CARTOON_PAYMENT\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":21,\"businessTagName\":\"打赏\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":10,\"incomeTypeName\":\"打赏收入\",\"incomeTypeEnum\":\"REWARD\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":18,\"businessTagName\":\"随刻微剧\",\"totalShareAmount\":10.73,\"projectShareAmount\":10.73,\"incomeList\":[{\"incomeType\":16,\"incomeTypeName\":\"激励广告充值麒麟\",\"incomeTypeEnum\":\"INCENTIVE_AD_RECHARGE\",\"shareAmount\":3.15,\"show\":true},{\"incomeType\":15,\"incomeTypeName\":\"激励广告分成\",\"incomeTypeEnum\":\"INCENTIVE_AD_SHARE\",\"shareAmount\":2.75,\"show\":true},{\"incomeType\":18,\"incomeTypeName\":\"付费充值麒麟\",\"incomeTypeEnum\":\"CASH_PAY_RECHARGE\",\"shareAmount\":1.5,\"show\":true},{\"incomeType\":17,\"incomeTypeName\":\"付费现金分成\",\"incomeTypeEnum\":\"CASH_PAY_SHARE\",\"shareAmount\":3.33,\"show\":true}]}]},\"month\":{\"totalShareAmount\":924273.632,\"data\":[{\"businessTag\":4,\"businessTagName\":\"儿童\",\"totalShareAmount\":87758.54,\"projectShareAmount\":87758.54,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":9122.5,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":400,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":68065.36,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":10170.68,\"show\":true}]},{\"businessTag\":5,\"businessTagName\":\"综艺\",\"totalShareAmount\":87558.54,\"projectShareAmount\":87558.54,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":9122.5,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":200,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":68065.36,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":10170.68,\"show\":true}]},{\"businessTag\":11,\"businessTagName\":\"竖短片/短剧\",\"totalShareAmount\":191937.822,\"projectShareAmount\":191937.822,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":13683.75,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":900,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":153738.24,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":23615.832,\"show\":true}]},{\"businessTag\":6,\"businessTagName\":\"纪录片\",\"totalShareAmount\":131437.81,\"projectShareAmount\":131437.81,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":13683.75,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":400,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":102098.04,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":15256.02,\"show\":true}]},{\"businessTag\":12,\"businessTagName\":\"游戏短视频\",\"totalShareAmount\":131637.81,\"projectShareAmount\":131637.81,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":13683.75,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":600,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":102098.04,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":15256.02,\"show\":true}]},{\"businessTag\":1,\"businessTagName\":\"电视剧\",\"totalShareAmount\":41649.62,\"projectShareAmount\":41805.4,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":9122.5,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":400,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":28444.22,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":200,\"show\":true},{\"incomeType\":5,\"incomeTypeName\":\"海外发行收入\",\"incomeTypeEnum\":\"OVERSEA_ISSUE\",\"shareAmount\":3482.9,\"show\":true}]},{\"businessTag\":2,\"businessTagName\":\"电影\",\"totalShareAmount\":84772.0,\"projectShareAmount\":84772.0,\"incomeList\":[{\"incomeType\":6,\"incomeTypeName\":\"单点收入\",\"incomeTypeEnum\":\"DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":9122.5,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":400,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":28600.22,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":200,\"show\":true},{\"incomeType\":5,\"incomeTypeName\":\"海外发行收入\",\"incomeTypeEnum\":\"OVERSEA_ISSUE\",\"shareAmount\":46449.28,\"show\":true}]},{\"businessTag\":3,\"businessTagName\":\"动漫\",\"totalShareAmount\":166900.25,\"projectShareAmount\":166900.25,\"incomeList\":[{\"incomeType\":6,\"incomeTypeName\":\"单点收入\",\"incomeTypeEnum\":\"DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":18245,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":600,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":132297.28,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":15757.97,\"show\":true}]},{\"businessTag\":8,\"businessTagName\":\"UID制(视频广告)\",\"totalShareAmount\":600,\"projectShareAmount\":600,\"incomeList\":[{\"incomeType\":7,\"incomeTypeName\":\"信息流广告收入\",\"incomeTypeEnum\":\"FEED\",\"shareAmount\":200,\"show\":true},{\"incomeType\":9,\"incomeTypeName\":\"小视频广告收入\",\"incomeTypeEnum\":\"SMALL_VIDEO\",\"shareAmount\":200,\"show\":true},{\"incomeType\":8,\"incomeTypeName\":\"贴片广告收入\",\"incomeTypeEnum\":\"PASTE\",\"shareAmount\":200,\"show\":true}]},{\"businessTag\":60,\"businessTagName\":\"文学\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":12,\"incomeTypeName\":\"文学非稿费\",\"incomeTypeEnum\":\"BOOK_UN_PAYMENT\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":70,\"businessTagName\":\"漫画\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":14,\"incomeTypeName\":\"漫画单订\",\"incomeTypeEnum\":\"CARTOON_DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":13,\"incomeTypeName\":\"漫画稿费\",\"incomeTypeEnum\":\"CARTOON_PAYMENT\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":21,\"businessTagName\":\"打赏\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":10,\"incomeTypeName\":\"打赏收入\",\"incomeTypeEnum\":\"REWARD\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":18,\"businessTagName\":\"随刻微剧\",\"totalShareAmount\":21.24,\"projectShareAmount\":21.24,\"incomeList\":[{\"incomeType\":16,\"incomeTypeName\":\"激励广告充值麒麟\",\"incomeTypeEnum\":\"INCENTIVE_AD_RECHARGE\",\"shareAmount\":6.3,\"show\":true},{\"incomeType\":15,\"incomeTypeName\":\"激励广告分成\",\"incomeTypeEnum\":\"INCENTIVE_AD_SHARE\",\"shareAmount\":5.5,\"show\":true},{\"incomeType\":18,\"incomeTypeName\":\"付费充值麒麟\",\"incomeTypeEnum\":\"CASH_PAY_RECHARGE\",\"shareAmount\":3.0,\"show\":true},{\"incomeType\":17,\"incomeTypeName\":\"付费现金分成\",\"incomeTypeEnum\":\"CASH_PAY_SHARE\",\"shareAmount\":6.44,\"show\":true}]}]},\"total\":{\"totalShareAmount\":1462134.566,\"data\":[{\"businessTag\":4,\"businessTagName\":\"儿童\",\"totalShareAmount\":131637.81,\"projectShareAmount\":131637.81,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":13683.75,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":600,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":102098.04,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":15256.02,\"show\":true}]},{\"businessTag\":5,\"businessTagName\":\"综艺\",\"totalShareAmount\":131437.81,\"projectShareAmount\":131437.81,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":13683.75,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":400,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":102098.04,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":15256.02,\"show\":true}]},{\"businessTag\":11,\"businessTagName\":\"竖短片/短剧\",\"totalShareAmount\":255917.096,\"projectShareAmount\":255917.096,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":18245,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":1200,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":204984.32,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":31487.776,\"show\":true}]},{\"businessTag\":6,\"businessTagName\":\"纪录片\",\"totalShareAmount\":175117.08,\"projectShareAmount\":175117.08,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":18245,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":400,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":136130.72,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":20341.36,\"show\":true}]},{\"businessTag\":12,\"businessTagName\":\"游戏短视频\",\"totalShareAmount\":175517.08,\"projectShareAmount\":175517.08,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":18245,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":800,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":136130.72,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":20341.36,\"show\":true}]},{\"businessTag\":1,\"businessTagName\":\"电视剧\",\"totalShareAmount\":90526.38,\"projectShareAmount\":89650.05,\"incomeList\":[{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":13683.75,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":4099,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":29277.33,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":500,\"show\":true},{\"incomeType\":5,\"incomeTypeName\":\"海外发行收入\",\"incomeTypeEnum\":\"OVERSEA_ISSUE\",\"shareAmount\":42966.3,\"show\":true}]},{\"businessTag\":2,\"businessTagName\":\"电影\",\"totalShareAmount\":255473.11,\"projectShareAmount\":255473.11,\"incomeList\":[{\"incomeType\":6,\"incomeTypeName\":\"单点收入\",\"incomeTypeEnum\":\"DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":13683.75,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":600,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":42900.33,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":300,\"show\":true},{\"incomeType\":5,\"incomeTypeName\":\"海外发行收入\",\"incomeTypeEnum\":\"OVERSEA_ISSUE\",\"shareAmount\":197989.03,\"show\":true}]},{\"businessTag\":3,\"businessTagName\":\"动漫\",\"totalShareAmount\":240978.76,\"projectShareAmount\":241480.71,\"incomeList\":[{\"incomeType\":6,\"incomeTypeName\":\"单点收入\",\"incomeTypeEnum\":\"DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":1,\"incomeTypeName\":\"EV收入\",\"incomeTypeEnum\":\"EV\",\"shareAmount\":22806.25,\"show\":true},{\"incomeType\":2,\"incomeTypeName\":\"IPM收入\",\"incomeTypeEnum\":\"IPM\",\"shareAmount\":800,\"show\":true},{\"incomeType\":3,\"incomeTypeName\":\"时长收入\",\"incomeTypeEnum\":\"PD\",\"shareAmount\":196529.2,\"show\":true},{\"incomeType\":4,\"incomeTypeName\":\"奖励金收入\",\"incomeTypeEnum\":\"PD_REWARD\",\"shareAmount\":20843.31,\"show\":true}]},{\"businessTag\":8,\"businessTagName\":\"UID制(视频广告)\",\"totalShareAmount\":5369,\"projectShareAmount\":5369,\"incomeList\":[{\"incomeType\":7,\"incomeTypeName\":\"信息流广告收入\",\"incomeTypeEnum\":\"FEED\",\"shareAmount\":1060,\"show\":true},{\"incomeType\":9,\"incomeTypeName\":\"小视频广告收入\",\"incomeTypeEnum\":\"SMALL_VIDEO\",\"shareAmount\":3109,\"show\":true},{\"incomeType\":8,\"incomeTypeName\":\"贴片广告收入\",\"incomeTypeEnum\":\"PASTE\",\"shareAmount\":1200,\"show\":true}]},{\"businessTag\":60,\"businessTagName\":\"文学\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":12,\"incomeTypeName\":\"文学非稿费\",\"incomeTypeEnum\":\"BOOK_UN_PAYMENT\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":70,\"businessTagName\":\"漫画\",\"totalShareAmount\":0,\"projectShareAmount\":0,\"incomeList\":[{\"incomeType\":14,\"incomeTypeName\":\"漫画单订\",\"incomeTypeEnum\":\"CARTOON_DEMAND\",\"shareAmount\":0,\"show\":true},{\"incomeType\":13,\"incomeTypeName\":\"漫画稿费\",\"incomeTypeEnum\":\"CARTOON_PAYMENT\",\"shareAmount\":0,\"show\":true}]},{\"businessTag\":21,\"businessTagName\":\"打赏\",\"totalShareAmount\":100.0,\"projectShareAmount\":100.0,\"incomeList\":[{\"incomeType\":10,\"incomeTypeName\":\"打赏收入\",\"incomeTypeEnum\":\"REWARD\",\"shareAmount\":100.0,\"show\":true}]},{\"businessTag\":18,\"businessTagName\":\"随刻微剧\",\"totalShareAmount\":60.44,\"projectShareAmount\":60.44,\"incomeList\":[{\"incomeType\":16,\"incomeTypeName\":\"激励广告充值麒麟\",\"incomeTypeEnum\":\"INCENTIVE_AD_RECHARGE\",\"shareAmount\":19.15,\"show\":true},{\"incomeType\":15,\"incomeTypeName\":\"激励广告分成\",\"incomeTypeEnum\":\"INCENTIVE_AD_SHARE\",\"shareAmount\":19.05,\"show\":true},{\"incomeType\":18,\"incomeTypeName\":\"付费充值麒麟\",\"incomeTypeEnum\":\"CASH_PAY_RECHARGE\",\"shareAmount\":8.56,\"show\":true},{\"incomeType\":17,\"incomeTypeName\":\"付费现金分成\",\"incomeTypeEnum\":\"CASH_PAY_SHARE\",\"shareAmount\":13.68,\"show\":true}]}]}}";
        System.out.println(Json2BeanUtils.generate(json));
    }
}
