package io.dq.dynamic;

import com.google.common.reflect.TypeToken;
import io.dq.annotation.Beta;
import io.dq.annotation.value.CodeStatus;
import io.dq.domain.Condition;
import io.dq.util.AnnotationUtil;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Beta(status = {CodeStatus.IN_PROGRESS, CodeStatus.POTENTIALLY_DEPRECATED})
@Getter
public class BooleanExpression<T> {

    private List<Condition<T>> conditions = new ArrayList<>();
    private TypeToken<T> typeToken;
    private Class<T> clazz;

    public BooleanExpression() {

        this.typeToken = TypeToken.of(clazz);
        this.clazz = (Class<T>) typeToken.getRawType();
    }

    public BooleanExpression(Class<T> clazz) {
        this.clazz = clazz;
    }

    public BooleanExpression<T> eq(@NotNull Function<T, ?> valueExtractor, Object value) {
        String sqlCondition = String.format("%s = '%s'", getFieldName(valueExtractor), value);
        Predicate<T> predicate = item -> valueExtractor.apply(item).equals(value);
        conditions.add(new Condition<>(predicate, sqlCondition));
        return this;
    }

    public BooleanExpression<T> notEq(@NotNull Function<T, ?> valueExtractor, Object value) {
        String sqlCondition = String.format("%s <> '%s'", getFieldName(valueExtractor), value);
        Predicate<T> predicate = item -> !valueExtractor.apply(item).equals(value);
        conditions.add(new Condition<>(predicate, sqlCondition));
        return this;
    }

    public BooleanExpression<T> and(@NotNull Function<T, ?> valueExtractor, Object value) {
        String sqlCondition = String.format("%s = '%s'", getFieldName(valueExtractor), value);
        Predicate<T> predicate = item -> valueExtractor.apply(item).equals(value);
        conditions.add(new Condition<>(predicate, sqlCondition));
        return this;
    }

    public BooleanExpression<T> and(@NotNull BooleanExpression<T> other) {
        String sqlCondition = String.format("(%s) AND (%s)", this.toSql(), other.toSql());
        conditions.add(new Condition<>(item -> this.matches(item) && other.matches(item), sqlCondition));
        return this;
    }

    public BooleanExpression<T> or(@NotNull Function<T, ?> valueExtractor, Object value) {
        String sqlCondition = String.format("%s = '%s'", getFieldName(valueExtractor), value);
        Predicate<T> predicate = item -> valueExtractor.apply(item).equals(value);
        conditions.add(new Condition<>(predicate, sqlCondition));
        return this;
    }

    public BooleanExpression<T> in(@NotNull Function<T, ?> valueExtractor, @NotNull List<?> values) {
        String sqlCondition = String.format("(%s) IN (%s)",
                getFieldName(valueExtractor),
                values.stream().map(Object::toString).collect(Collectors.joining(", ")));
        Predicate<T> predicate = item -> values.contains(valueExtractor.apply(item));
        conditions.add(new Condition<>(predicate, sqlCondition));
        return this;
    }

    public BooleanExpression<T> notIn(@NotNull Function<T, ?> valueExtractor, @NotNull List<?> values) {
        String sqlCondition = String.format("(%s) NOT IN (%s)",
                getFieldName(valueExtractor),
                values.stream().map(Object::toString).collect(Collectors.joining(", ")));
        Predicate<T> predicate = item -> !values.contains(valueExtractor.apply(item));
        conditions.add(new Condition<>(predicate, sqlCondition));
        return this;
    }

    public static <T> @NotNull BooleanExpression<T> anyOf(BooleanExpression<T>... expressions) {

        BooleanExpression<T> booleanExpression = new BooleanExpression<>();

        String joinedExpressions = Arrays.stream(expressions)
                .map(BooleanExpression::toSql)
                .collect(Collectors.joining(" OR "));

        // 새로운 Condition 추가
        booleanExpression.conditions.add(new Condition<>(
                item -> Arrays.stream(expressions).anyMatch(expression -> expression.evaluate(item)),
                "(" + joinedExpressions + ")"
        ));

        return booleanExpression;
    }

    public static <T> @NotNull BooleanExpression<T> allOf(BooleanExpression<T>... expressions) {
        // 새로운 BooleanExpression 생성
        BooleanExpression<T> booleanExpression = new BooleanExpression<>();

        String joinedExpressions = Arrays.stream(expressions)
                .map(BooleanExpression::toSql)
                .collect(Collectors.joining(" AND "));

        // 새로운 Condition 추가
        booleanExpression.conditions.add(new Condition<>(
                item -> Arrays.stream(expressions).allMatch(expression -> expression.evaluate(item)),
                "(" + joinedExpressions + ")"
        ));

        return booleanExpression; // 새로 생성한 BooleanExpression 반환
    }

    public String toSql() {
        return conditions.stream()
                .map(Condition::sqlCondition)
                .collect(Collectors.joining(" AND ")); // 기본적으로 AND로 조건 결합
    }

    private boolean matches(T item) {
        return conditions.stream()
                .allMatch(condition -> condition.predicate().test(item));
    }

    private boolean expressionsMatch(T item, BooleanExpression<T>[] expressions) {
        return Arrays.stream(expressions)
                .allMatch(expression -> expression.evaluate(item));
    }

    public boolean evaluate(T item) {
        // 모든 조건을 평가하여 true 또는 false 반환
        return conditions.stream().allMatch(condition -> condition.evaluate(item));
    }

    private String getFieldName(Function<T, ?> valueExtractor) {
        return AnnotationUtil.getFieldAnnotationName(clazz, valueExtractor);
    }

}