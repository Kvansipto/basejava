package com.urise.webapp.model;

import java.util.Date;
import java.util.Objects;

public class Company {
    private final Date dateBegin;
    private final Date dateEnd;
    private final String companyName;
    private final String position;
    private final String content;

    public Company(Date dateBegin, Date dateEnd, String companyName, String position, String content) {
        this.dateBegin = dateBegin;
        this.dateEnd = dateEnd;
        this.companyName = companyName;
        this.position = position;
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(dateBegin, company.dateBegin) && Objects.equals(dateEnd, company.dateEnd) && Objects.equals(companyName, company.companyName) && Objects.equals(position, company.position) && Objects.equals(content, company.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateBegin, dateEnd, companyName, position, content);
    }

    @Override
    public String toString() {
        return "Company{" +
                "dateBegin=" + dateBegin +
                ", dateEnd=" + dateEnd +
                ", companyName='" + companyName + '\'' +
                ", position='" + position + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
