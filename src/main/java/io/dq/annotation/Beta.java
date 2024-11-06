package io.dq.annotation;

import io.dq.annotation.value.CodeStatus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Target({METHOD, TYPE, TYPE_PARAMETER, FIELD})
@Retention(RetentionPolicy.SOURCE)
public @interface Beta {

    CodeStatus[] status() default {CodeStatus.IN_PROGRESS}; // 개발 상태

    String developer() default "Unknown"; // 개발자 이름

    String expectedRelease() default ""; // 예상 출시 버전

    String lastModified() default ""; // 마지막 수정 날짜

    String reviewedBy() default ""; // 리뷰 담당자

    boolean isExperimental() default false; // 실험적 기능 여부 - 이 기능이 실험적으로 추가된 것인가?

    String notes() default ""; // 추가 설명

    /*
    예시:

    @Beta(
            status = {CodeStatus.IN_PROGRESS, CodeStatus.SUBJECT_TO_CHANGE},
            developer = "Ahn Nayeong",
            expectedRelease = "1.5.0",
            lastModified = "2024-11-04",
            isExperimental = true,
            notes = "이 메소드는 큰 변경이 있을 수 있습니다."
    )
    */

}
