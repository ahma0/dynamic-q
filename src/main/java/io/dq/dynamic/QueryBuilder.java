package io.dq.dynamic;

import io.dq.annotation.Beta;
import io.dq.annotation.value.CodeStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * querydsl을 쓸 수 없어서 간단하게 만든 클래스입니다..
 */
@Beta(status = {CodeStatus.IN_PROGRESS, CodeStatus.POTENTIALLY_DEPRECATED})
public class QueryBuilder {

    private final StringBuilder query = new StringBuilder();
    private final List<BooleanExpression> expressions = new ArrayList<>();

    public static final String ALL = "*";

    private QueryBuilder() {
    }

    @Contract(" -> new")
    public static @NotNull QueryBuilder of() {
        return new QueryBuilder();
    }

//    public void fetch(@NotNull DBManager dbManager) {
//        dbManager.execute(query.toString(), null);
//    }
//
//    public List<Object> fetchAndGetResult(@NotNull DBManager dbManager) {
//        dbManager.execute(query.toString(), null);
//    }
//
//    public List<Object> fetchAndGetResults(@NotNull DBManager dbManager) {
//        dbManager.execute(query.toString(), null);
//    }

    public String fetchToQuery() {
        if (query.toString().contains("SELECT")) {
            String whereClause = buildWhereClause();
            if (!whereClause.isEmpty()) {
                query.append("\nWHERE ").append(whereClause).append(" ");
            }
        }
        return query.toString().trim();
    }

    public QueryBuilder select(String s) {
        query.append("SELECT ").append(s);
        return this;
    }

    public QueryBuilder selectFrom(String tableName) {
        query.append("SELECT * FROM ").append(tableName);
        return this;
    }

    public QueryBuilder from(String tableName) {
        query.append("\nFROM ").append(tableName);
        return this;
    }

    public QueryBuilder where(BooleanExpression booleanExpression) {
        expressions.add(booleanExpression);
        query.append("\nWHERE ").append(booleanExpression.toSql());
        return this;
    }

    public QueryBuilder transform() {
        return this;
    }

    public String all() {
        return ALL;
    }

    public void print() {
        System.out.println(query.toString());
    }

    private String buildWhereClause() {
        return expressions.stream()
                .map(BooleanExpression::toSql)
                .collect(Collectors.joining(" AND "));
    }

}
