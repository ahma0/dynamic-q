package io.dq.domain;

import kr.co.jirandata.crawler.common.annotation.Beta;
import kr.co.jirandata.crawler.common.annotation.value.CodeStatus;
import kr.co.jirandata.crawler.common.data.HtmlLink;
import kr.co.jirandata.crawler.common.sql.dynamic.BooleanExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Beta(status = {CodeStatus.IN_PROGRESS, CodeStatus.POTENTIALLY_DEPRECATED})
public class QHtmlLink extends QBase<HtmlLink> {

    public QHtmlLink() {
        super(HtmlLink.class);
    }

    public BooleanExpression<HtmlLink> isJavascriptFalseThenCompareUrl(List<HtmlLink> htmlLinks) {
        return isFalse(HtmlLink::getJavascriptStr)
                .and(in(HtmlLink::getJavascriptStr, htmlLinks));
    }

    public BooleanExpression<HtmlLink> isJavascriptTrueThenCompareParentUrlAndJavascriptStr(@NotNull List<HtmlLink> htmlLinks) {

        List<Tuple<String, String>> tuples = htmlLinks.stream()
                .filter(htmlLink -> htmlLink.isJavascript())
                .map(htmlLink -> Tuple.of(htmlLink.getJavascriptStr(), htmlLink.getParentUrl().getUrl()))
                .toList();

        return isTrue(HtmlLink::getJavascriptStr)
                .and(in(
                        htmlLink -> Tuple.of(htmlLink.getJavascriptStr(), htmlLink.getParentUrl().getUrl()),
                        List.of(tuples)
                ));
    }

    public BooleanExpression<HtmlLink> isDuplicateHtmlLink(List<HtmlLink> htmlLinks) {
        return BooleanExpression.anyOf(
                isJavascriptFalseThenCompareUrl(htmlLinks),
                isJavascriptTrueThenCompareParentUrlAndJavascriptStr(htmlLinks)
        );
    }
}
