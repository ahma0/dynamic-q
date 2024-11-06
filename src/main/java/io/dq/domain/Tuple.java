package io.dq.domain;

import kr.co.jirandata.crawler.common.annotation.Beta;
import kr.co.jirandata.crawler.common.annotation.value.CodeStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Beta(status = {CodeStatus.IN_PROGRESS, CodeStatus.POTENTIALLY_DEPRECATED})
public record Tuple<T1, T2>(T1 item1, T2 item2) {

    @Contract(value = "_, _ -> new", pure = true)
    public static <T1, T2> @NotNull Tuple<T1, T2> of(T1 item1, T2 item2) {
        return new Tuple<>(item1, item2);
    }
}
