package io.dq.domain;

import java.util.function.Predicate;

/**
 * @param predicate    필터링 조건
 * @param sqlCondition SQL 조건 문자열
 */
public record Condition<T>(Predicate<T> predicate, String sqlCondition) {

    public boolean evaluate(T item) {
        return predicate.test(item);
    }

}
