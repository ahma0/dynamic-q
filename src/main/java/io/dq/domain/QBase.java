package io.dq.domain;

import io.dq.annotation.Beta;
import io.dq.annotation.Column;
import io.dq.annotation.value.CodeStatus;
import io.dq.dynamic.BooleanExpression;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

@Beta(status = {CodeStatus.IN_PROGRESS, CodeStatus.POTENTIALLY_DEPRECATED})
public abstract class QBase<T> {

    private BooleanExpression<T> booleanExpression;
    private QMetadata<T> metadata = new QMetadata<>();
//    private BooleanExpression<T> booleanExpression = new BooleanExpression<>();

    public QBase() {
    }

    public QBase(Class<T> clazz) {
        this.booleanExpression = new BooleanExpression<>(clazz);
        initializeMetadata(clazz);
    }

    @Contract("_ -> new")
    public static <T> @NotNull QBase<T> create() {
        return new QBase<T>() { /* Optional overrides */
        };
    }

    @Contract("_ -> new")
    public static <T> @NotNull QBase<T> create(Class<T> clazz) {
        return new QBase<T>(clazz) { /* Optional overrides */
        };
    }

    private void initializeMetadata(Class<T> clazz) {
        try {
            // `T` 타입의 필드들 가져오기
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                Column annotation = field.getAnnotation(Column.class);
                if (annotation != null) {
                    // 어노테이션 값 저장
                    metadata.addFieldAnnotation(field.getName(), annotation.name());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String fetchToString() {
        return booleanExpression.toSql();
    }

    public BooleanExpression<T> eq(@NotNull Function<T, ?> valueExtractor, Object value) {
        return booleanExpression.eq(valueExtractor, value);
    }

    public BooleanExpression<T> notEq(@NotNull Function<T, ?> valueExtractor, Object value) {
        return booleanExpression.notEq(valueExtractor, value);
    }

    public BooleanExpression<T> isTrue(@NotNull Function<T, ?> valueExtractor) {
        return booleanExpression.eq(valueExtractor, true);
    }

    public BooleanExpression<T> isFalse(@NotNull Function<T, ?> valueExtractor) {
        return booleanExpression.eq(valueExtractor, false);
    }

    public BooleanExpression<T> and(@NotNull Function<T, ?> valueExtractor, Object value) {
        return booleanExpression.and(booleanExpression.and(valueExtractor, value));
    }

    public BooleanExpression<T> and(BooleanExpression<T> other) {
        return booleanExpression.and(other);
    }

    public BooleanExpression<T> or(@NotNull Function<T, ?> valueExtractor, Object value) {
        return booleanExpression.or(valueExtractor, value);
    }

    public BooleanExpression<T> in(@NotNull Function<T, ?> valueExtractor, @NotNull List<?> values) {
        return booleanExpression.in(valueExtractor, values);
    }

    public BooleanExpression<T> notIn(@NotNull Function<T, ?> valueExtractor, @NotNull List<?> values) {
        return booleanExpression.notIn(valueExtractor, values);
    }

}

