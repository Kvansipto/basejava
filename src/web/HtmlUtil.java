package web;

import com.urise.webapp.model.*;

import java.time.LocalDate;

public class HtmlUtil {

    public static String toHtml(ContactType contactType, String value) {
        switch (contactType) {
            case Skype -> {
                return contactType.getTitle() + ": " + toLink("skype:" + value, value);
            }
            case EMAIL -> {
                return (contactType.getTitle() + ": " + toLink("email:" + value, value));
            }
            case LINKEDIN, GITHUB, STACKOVERFLOW, HOMEPAGE -> {
                return toLink(value, contactType.getTitle());
            }
        }
        return (value == null) ? "" : contactType.getTitle() + ": " + value;
    }

    public static String toHtml(SectionType sectionType, Section section) {
        switch (sectionType) {
            case PERSONAL, OBJECTIVE -> {
                return (((TextSection) section).getContent());
            }
            case QUALIFICATIONS, ACHIEVEMENT -> {
                StringBuilder sb = new StringBuilder();
                for (String s : ((ListSection) section).getContent()) {
                    sb.append(s).append("\n");
                }
                sb.replace(sb.lastIndexOf("\n"), sb.length(), "");
                return String.valueOf(sb);
            }
            case EXPERIENCE, EDUCATION -> {
                return "pusto";
            }
        }
        return null;
    }

    public static String toHtml(String value) {
        return toLink(value, value);
    }

    public static String toHtml(SectionType sectionType) {
        return "<h3>" + sectionType.getTitle() + "</h3>";
    }

    private static String toLink(String href, String title) {
        return "<a href='" + href + "'>" + title + "</a>";
    }

    public static String toHtml(Company.Period period) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb
                .append(toHtmlDate(period.getDateBegin()))
                .append(" - ")
                .append(toHtmlDate(period.getDateEnd())));
    }

    private static String toHtmlDate(LocalDate date) {
        StringBuilder sb = new StringBuilder();
        return String.valueOf(sb
                .append(date.getMonth())
                .append(" ")
                .append(date.getYear()));
    }
}
