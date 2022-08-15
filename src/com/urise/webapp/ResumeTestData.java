package com.urise.webapp;

import com.urise.webapp.model.*;

import java.util.*;

public class ResumeTestData {
    public static void main(String[] args) {
        Resume r1 = new Resume("uuid5", "A");

        r1.contactMap.put(ContactType.EMAIL, "email");
        r1.contactMap.put(ContactType.PHONE, "+72873158212");
        r1.contactMap.put(ContactType.Skype, "dwbydu21");
        r1.contactMap.put(ContactType.GITHUB, "www.github.com");

        TextSection personal = new TextSection("personal content");
        TextSection objective = new TextSection("objective content");

        ListSection achievement = new ListSection(
                new ArrayList<>(Arrays.asList("first achieve", "second achieve", "third achieve")));

        ListSection qualifications = new ListSection(
                new ArrayList<>(Arrays.asList("first qualify", "second qualify", "third qualify")));

        GregorianCalendar dateBegin1 = new GregorianCalendar(1999, Calendar.NOVEMBER, 1);
        GregorianCalendar dateEnd1 = new GregorianCalendar(2007, Calendar.NOVEMBER, 1);
        GregorianCalendar dateBegin2 = new GregorianCalendar(2008, Calendar.NOVEMBER, 1);
        GregorianCalendar dateEnd2 = new GregorianCalendar(2010, Calendar.NOVEMBER, 1);

        Company.Period period1 = new Company.Period(dateBegin1, dateEnd1, "Manager", "did smth");
        Company.Period period2 = new Company.Period(dateBegin1, dateEnd1, "Manager1", "did smth1");
        Company.Period period3 = new Company.Period(dateBegin2, dateEnd2, "Manager2", "did smth2");

        ArrayList<Company.Period> periodArrayList1 = new ArrayList<>();
        ArrayList<Company.Period> periodArrayList2 = new ArrayList<>();

        periodArrayList1.add(period1);
        periodArrayList1.add(period2);
        periodArrayList2.add(period3);

        Company company1 = new Company("Company1", periodArrayList1);
        Company company2 = new Company("Company2", periodArrayList2);

        CompanySection experience = new CompanySection(Arrays.asList(company1, company2));

        CompanySection education = new CompanySection(Arrays.asList(company1, company2));

        r1.sectionMap.put(SectionType.PERSONAL, personal);
        r1.sectionMap.put(SectionType.OBJECTIVE, objective);
        r1.sectionMap.put(SectionType.ACHIEVEMENT, achievement);
        r1.sectionMap.put(SectionType.QUALIFICATIONS, qualifications);
        r1.sectionMap.put(SectionType.EXPERIENCE, experience);
        r1.sectionMap.put(SectionType.EDUCATION, education);

        for (Map.Entry<ContactType, String> entry : r1.contactMap.entrySet()) {
            System.out.println(entry.getKey().toString());
            System.out.println(entry.getValue());
        }

        for (Map.Entry<SectionType, Section> entry : r1.sectionMap.entrySet()) {
            System.out.println(entry.getKey().toString());
            System.out.println(entry.getValue().toString());
        }
    }
}
