package io.dq.annotation.value;

public enum CodeStatus {

    IN_PROGRESS("개발 진행 중"),
    COMPLETED("완료"),
    UNDER_REVIEW("검토 중"),
    NEEDS_REVISION("수정 필요"),
    TEST("테스트 중"),
    POTENTIALLY_DEPRECATED("잠재적으로 폐기될 가능성이 있음"),
    SUBJECT_TO_CHANGE("향후 변경 가능");

    private final String description;

    CodeStatus(String description) {
        this.description = description;
    }
}
