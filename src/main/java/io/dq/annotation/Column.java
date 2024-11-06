package io.dq.annotation;

import kr.co.jirandata.crawler.common.annotation.value.CodeStatus;
import kr.co.jirandata.crawler.common.annotation.value.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Beta(status = {CodeStatus.IN_PROGRESS, CodeStatus.POTENTIALLY_DEPRECATED})
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

    String name();

    FieldType fieldType() default FieldType.COLUMN;

}

