package com.urise.webapp.model;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

public class ListSection extends Section {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<String> content;

    public ListSection(List<String> content) {
        this.content = content;
    }

    public ListSection() {
    }

    public List<String> getContent() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListSection that = (ListSection) o;
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
