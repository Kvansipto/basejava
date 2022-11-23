package com.urise.webapp.model;

import com.urise.webapp.util.XmlLocalDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
public class Company implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private List<Period> periods;

    public Company(String name, List<Company.Period> periods) {
        this.name = name;
        this.periods = periods;
    }

    public Company() {
    }

    public String getName() {
        return name;
    }

    public List<Period> getPeriods() {
        return periods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(name, company.name) && Objects.equals(periods, company.periods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, periods);
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Period implements Serializable {

        @XmlJavaTypeAdapter(XmlLocalDateAdapter.class)
        private LocalDate dateBegin;
        @XmlJavaTypeAdapter(XmlLocalDateAdapter.class)
        private LocalDate dateEnd;
        private String title;
        private String description;

        public Period(LocalDate dateBegin, LocalDate dateEnd, String title, String description) {
            this.dateBegin = dateBegin;
            this.dateEnd = dateEnd;
            this.title = title;
            this.description = description;
        }

        public Period() {
        }

        public LocalDate getDateBegin() {
            return dateBegin;
        }

        public LocalDate getDateEnd() {
            return dateEnd;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Period{" +
                    "dateBegin=" + dateBegin +
                    ", dateEnd=" + dateEnd +
                    ", position='" + title + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Period period = (Period) o;
            return Objects.equals(dateBegin, period.dateBegin) && Objects.equals(dateEnd, period.dateEnd) && Objects.equals(title, period.title) && Objects.equals(description, period.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dateBegin, dateEnd, title, description);
        }
    }
}
