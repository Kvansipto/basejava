package com.urise.webapp.model;

import java.io.Serial;
import java.util.List;
import java.util.Objects;

public class CompanySection extends Section {

    @Serial
    private static final long serialVersionUID = 1L;

    private List<Company> companies;

    public CompanySection(List<Company> companies) {
        this.companies = companies;
    }

    public CompanySection() {
    }

    public List<Company> getCompanies() {
        return companies;
    }

    @Override
    public String toString() {
        return "CompanySection{" +
                "companies=" + companies +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanySection that = (CompanySection) o;
        return Objects.equals(companies, that.companies);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companies);
    }
}
