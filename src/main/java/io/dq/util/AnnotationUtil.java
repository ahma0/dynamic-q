package io.dq.util;

import io.dq.annotation.Beta;
import io.dq.annotation.Column;
import kr.co.jirandata.crawler.common.annotation.value.CodeStatus;
import kr.co.jirandata.crawler.common.sql.dynamic.domain.Tuple;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;

@Log4j2
@Beta(status = {CodeStatus.IN_PROGRESS, CodeStatus.POTENTIALLY_DEPRECATED})
public class AnnotationUtil {

//    public static <T, R> String getFieldName(Class<T> clazz, Function<T, R> function) {
//    }

    public static <T, R> String getFieldNameFromMethodReference(Function<T, R> getter) {
        try {
            Method writeReplace = getter.getClass().getDeclaredMethod("writeReplace");
            writeReplace.setAccessible(true);
            SerializedLambda lambda = (SerializedLambda) writeReplace.invoke(getter);

            String methodName = lambda.getImplMethodName();
            if (methodName.startsWith("get")) {
                methodName.replace("get", "");
            }

            return capitalizeFirstLetter(methodName);
        } catch (Exception e) {
            log.error("메서드 참조에서 필드 이름 추출 실패 {}", e.getMessage());
        }

        return null;
    }

    public static <T> @Nullable String getFieldAnnotationName(Class<T> clazz, Function<T, ?> getter) {
        try {
            String fieldName = getFieldNameFromMethodReference(getter);
            return extractFieldName(clazz, fieldName);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T, R> @Nullable List<String> getFieldNameFromMethodReferenceIfTuple(Function<T, R> getter) {
        try {
            R result = getter.apply((T) new Object());

            if (result instanceof Tuple) {
                Tuple<?, ?> tuple = (Tuple<?, ?>) result;

                String firstFieldName = extractFieldName(tuple.getClass(), "first");
                String secondFieldName = extractFieldName(tuple.getClass(), "second");

                return List.of(firstFieldName, secondFieldName);
            } else {
                return List.of(getFieldNameFromMethodReference(getter));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String extractFieldName(@NotNull Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field field = clazz.getDeclaredField(fieldName);

        if (field.isAnnotationPresent(Column.class)) {
            fieldName = field.getAnnotation(Column.class).name();
        }

        return fieldName;
    }


    @FunctionalInterface
    public interface MethodReference<T> {
        Method getMethod();
    }

    private static String capitalizeFirstLetter(@NotNull String str) {
        String firstLetter = str.substring(0, 1);

        if (Character.isUpperCase(firstLetter.charAt(0))) {
            str = firstLetter.toLowerCase() + str.substring(1);
        }

        return str;
    }
}
