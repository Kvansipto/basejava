package com.urise.webapp.model;

import java.util.List;
import java.util.Objects;

public class SectionContentList extends Section {

    private final List<String> content;

    public SectionContentList(List<String> content) {
        this.content = content;
    }

    public List<String> getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectionContentList that = (SectionContentList) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    @Override
    public String toString() {
        return content.toString();
    }
}
