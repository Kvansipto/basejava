package com.urise.webapp.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class Company implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final ArrayList<Period> periods;

    public Company(String name, ArrayList<Company.Period> periods) {
        this.name = name;
        this.periods = periods;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Period> getPeriods() {
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

    public static class Period implements Serializable {

        private final LocalDate dateBegin;
        private final LocalDate dateEnd;
        private final String title;
        private final String description;

        public Period(LocalDate dateBegin, LocalDate dateEnd, String title, String description) {
            this.dateBegin = dateBegin;
            this.dateEnd = dateEnd;
            this.title = title;
            this.description = description;
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
