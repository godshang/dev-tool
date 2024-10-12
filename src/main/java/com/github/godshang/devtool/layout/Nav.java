/* SPDX-License-Identifier: MIT */

package com.github.godshang.devtool.layout;

import com.github.godshang.devtool.page.Page;
import javafx.scene.Node;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

record Nav(String title,
           Node graphic,
           Class<? extends Page> pageClass,
           List<String> searchKeywords) {

    public static final Nav ROOT = new Nav("ROOT", null, null, null);

    private static final Set<Class<? extends Page>> TAGGED_PAGES = Set.of(

    );

    public Nav {
        Objects.requireNonNull(title, "title");
        searchKeywords = Objects.requireNonNullElse(searchKeywords, Collections.emptyList());
    }

    public boolean isGroup() {
        return pageClass == null;
    }

    public boolean matches(String filter) {
        Objects.requireNonNull(filter);
        return contains(title, filter)
                || (searchKeywords != null && searchKeywords.stream().anyMatch(keyword -> contains(keyword, filter)));
    }

    public boolean isTagged() {
        return pageClass != null && TAGGED_PAGES.contains(pageClass);
    }

    private boolean contains(String text, String filter) {
        return text.toLowerCase().contains(filter.toLowerCase());
    }
}