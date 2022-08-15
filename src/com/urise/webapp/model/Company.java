package com.urise.webapp.model;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Objects;

public class Company {
    private final String companyName;
    private final ArrayList<Period> periods;

    public static class Period {

        private final GregorianCalendar dateBegin;
        private final GregorianCalendar dateEnd;
        private final String position;
        private final String description;

        public Period(GregorianCalendar dateBegin, GregorianCalendar dateEnd, String position, String description) {
            this.dateBegin = dateBegin;
            this.dateEnd = dateEnd;
            this.position = position;
            this.description = description;
        }

        @Override
        public String toString() {
            return "Period{" +
                    "dateBegin=" + dateBegin +
                    ", dateEnd=" + dateEnd +
                    ", position='" + position + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Period period = (Period) o;
            return Objects.equals(dateBegin, period.dateBegin) && Objects.equals(dateEnd, period.dateEnd) && Objects.equals(position, period.position) && Objects.equals(description, period.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(dateBegin, dateEnd, position, description);
        }
    }

    public Company(String companyName, ArrayList<Company.Period> periods) {
        this.companyName = companyName;
        this.periods = periods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(companyName, company.companyName) && Objects.equals(periods, company.periods);
    }

    @Override
    public int hashCode() {
        return Objects.hash(companyName, periods);
    }
}
