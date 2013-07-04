package com.mysema.dropbook.views;

import com.google.common.collect.Lists;
import com.mysema.dropbook.core.Wisdom;
import com.yammer.dropwizard.views.View;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class ReportView extends View {

    List<WisdomItem> wisdoms;

    public ReportView(Iterator<String> quotes) {
        super("report.mustache");
        wisdoms = Lists.newArrayList();
        while (quotes.hasNext()) {
            wisdoms.add(new WisdomItem(quotes.next()));
        }
    }
    static class WisdomItem {
        final String quote;
        public WisdomItem(String quote) {
            this.quote = quote;
        }
    }
}
