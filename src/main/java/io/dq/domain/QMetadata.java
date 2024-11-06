package io.dq.domain;

import java.util.HashMap;
import java.util.Map;

public class QMetadata<T> {

    private final Map<String, String> fieldAnnotations = new HashMap<>();

    // 메타데이터에 어노테이션 값 저장
    public void addFieldAnnotation(String fieldName, String annotationValue) {
        fieldAnnotations.put(fieldName, annotationValue);
    }

    // 필드명에 대한 어노테이션 값 반환
    public String getAnnotationValue(String fieldName) {
        return fieldAnnotations.get(fieldName);
    }
}
