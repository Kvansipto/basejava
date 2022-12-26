package web;

import com.urise.webapp.model.*;

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
                return ("<h3>" + sectionType.getTitle() + "</h3>" + ((TextSection) section).getContent());
            }
            case QUALIFICATIONS, ACHIEVEMENT -> {
                StringBuilder sb = new StringBuilder("<h3>" + sectionType.getTitle() + "</h3>");
                for (String s : ((ListSection) section).getContent()) {
                    sb.append(s).append("<br/>");
                }
                sb.replace(sb.lastIndexOf("<"), sb.length(), "");
                return String.valueOf(sb);
            }
            case EXPERIENCE, EDUCATION -> {
                return "pusto";
            }
            default -> {
                return "pusto";
            }
        }
    }

    public static String toHtml(String value) {
        return toLink(value, value);
    }

    private static String toLink(String href, String title) {
        return "<a href='" + href + "'>" + title + "</a>";
    }
}
