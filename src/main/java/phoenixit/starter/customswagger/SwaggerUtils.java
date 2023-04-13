package phoenixit.starter.customswagger;

import com.googlecode.jsonrpc4j.JsonRpcParam;
import com.googlecode.jsonrpc4j.JsonRpcService;
import lombok.experimental.UtilityClass;
import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

@UtilityClass
public class SwaggerUtils {

    static final Map<String, Object> modelsMap = new HashMap<>();

    static final Map<String, Class<?>> classesToAdd = new HashMap<>();

    public static Map<String, Object> getMethodsDescription() {
        Reflections reflections = new Reflections();
        Set<Class<?>> annotatedTypes = reflections.getTypesAnnotatedWith(JsonRpcService.class);
        List<Class<?>> rpcClasses = annotatedTypes.stream()
                .filter(Class::isInterface)
                .filter(clazz -> !clazz.isAssignableFrom(SwaggerRpc.class))
                .collect(Collectors.toList());

        Map<String, Object> result = new TreeMap<>();

        rpcClasses.forEach(clazz -> {
            JsonRpcService classAnnotation = clazz.getAnnotation(JsonRpcService.class);

            String entrypoint = classAnnotation.value();

            List<MethodDescription> methodDescriptions = new ArrayList<>();

            Arrays.stream(clazz.getDeclaredMethods()).forEach(method -> {
                MethodDescription methodDescription = new MethodDescription();
                methodDescription.setMethodName(method.getName());

                Map<String, Object> map = Arrays.stream(method.getParameters())
                        .collect(Collectors.toMap(
                                parameter -> parameter.getAnnotation(JsonRpcParam.class).value(),
                                SwaggerUtils::getParamDescription
                        ));

                methodDescription.setParams(map);

                methodDescriptions.add(methodDescription);
            });

            result.put(entrypoint, methodDescriptions);
        });

        addClassesToModels();
        result.put("models", modelsMap);

        return result;
    }

    public static Object getParamDescription(Parameter parameter) {
        if (parameter.getType().isPrimitive()) {
            return Map.of(parameter.getName(), parameter.getType().getSimpleName());
        }

        if (parameter.getType().isEnum()) {
            putToModels(parameter.getType());
            return parameter.getType().getSimpleName();
        }

        if (Collection.class.isAssignableFrom(parameter.getType())) {
            return getParameterCollectionString(parameter);
        }

        if (Map.class.isAssignableFrom(parameter.getType())) {
            return getParameterMapString(parameter);
        }

        if (!parameter.getType().getPackage().getName().contains("java")) {
            return getFields(parameter.getType());
        }

        return parameter.getType().getSimpleName();
    }

    private static void putToModels(Class<?> clazz) {
        modelsMap.put(
                clazz.getSimpleName(),
                Arrays.stream(clazz.getEnumConstants())
                        .map(Object::toString)
                        .collect(Collectors.toList())
        );
    }

    private static Map<String, Object> getFields(Class<?> clazz) {
        HashMap<String, Object> objectFieldsMap = new LinkedHashMap<>();

        Arrays.stream(clazz.getDeclaredFields()).forEach(
                field -> objectFieldsMap.putAll(getFieldDescription(field))
        );

        return objectFieldsMap;
    }

    private static String getParameterCollectionString(Parameter parameter) {
        return parameter.getType().getSimpleName() + "<" + getParameterType(parameter) + ">";
    }

    private static String getParameterMapString(Parameter parameter) {
        return "Map<" + getParameterType(parameter) + ">";
    }

    public static Map<String, Object> getFieldDescription(Field field) {
        if (field.getType().isPrimitive()) {
            return Map.of(field.getName(), field.getType().getSimpleName());
        }

        if (field.getType().isEnum()) {
            putToModels(field.getType());
            return Map.of(field.getName(), field.getType().getSimpleName());
        }

        if (Collection.class.isAssignableFrom(field.getType())) {
            return Map.of(field.getName(), getFieldCollectionString(field));
        }

        if (Map.class.isAssignableFrom(field.getType())) {
            return Map.of(field.getName(), getFieldMapString(field));
        }

        if (!field.getType().getPackage().getName().contains("java")) {
            return Map.of(field.getName(), getFields(field.getType()));
        }

        return Map.of(field.getName(), field.getType().getSimpleName());
    }

    private static String getFieldCollectionString(Field field) {
        return field.getType().getSimpleName() + "<" + getFieldType(field) + ">";
    }

    private static String getFieldMapString(Field field) {
        return "Map<" + getFieldType(field) + ">";
    }

    private static String getFieldType(Field field) {
        return getType((ParameterizedType) field.getGenericType());
    }

    private static String getParameterType(Parameter parameter) {
        return getType((ParameterizedType) parameter.getParameterizedType());
    }

    private static String getType(ParameterizedType parameterizedType) {
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        List<String> types = new ArrayList<>();
        Arrays.stream(typeArguments).forEach(type -> {
            if (type instanceof ParameterizedType) {
                types.add(((Class<?>) ((ParameterizedType) type).getRawType()).getSimpleName() +
                        "<" + getType((ParameterizedType) type) + ">");
            } else {
                Class<?> clazz = (Class<?>) type;
                types.add(clazz.getSimpleName());

                if (!clazz.getPackage().getName().contains("java.lang")) {
                    classesToAdd.put(clazz.getSimpleName(), clazz);
                }
            }
        });

        return String.join(", ", types);
    }

    private static void addClassesToModels() {
        classesToAdd.forEach((name, clazz) -> {
            if (!modelsMap.containsKey(name)) {
                modelsMap.put(name, getClassDescription(clazz));
            }
        });
    }

    public static Object getClassDescription(Class<?> clazz) {
        if (clazz.isEnum()) {
            return Arrays.stream(clazz.getEnumConstants())
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }

        return getFields(clazz);
    }
}
