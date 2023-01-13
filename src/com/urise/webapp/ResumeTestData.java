package com.urise.webapp;

import com.urise.webapp.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class ResumeTestData {

    public static Resume createResume(String uuid, String fullName) {
        Resume r1 = new Resume(uuid, fullName);

        r1.contactMap.put(ContactType.EMAIL, "email@dsad.sadd");
        r1.contactMap.put(ContactType.PHONE, "+72873158212");
        r1.contactMap.put(ContactType.Skype, "dwbydu21");
        r1.contactMap.put(ContactType.GITHUB, "www.github.com");

        TextSection personal = new TextSection("personal content");
        TextSection objective = new TextSection("objective content");

        ListSection achievement = new ListSection(
                new ArrayList<>(Arrays.asList("first achieve", "second achieve", "third achieve")));

        ListSection qualifications = new ListSection(
                new ArrayList<>(Arrays.asList("first qualify", "second qualify", "third qualify")));

        LocalDate dateBegin1 = LocalDate.of(1999, 10, 1);
        LocalDate dateEnd1 = LocalDate.of(2007, 10, 1);
        LocalDate dateBegin2 = LocalDate.of(2008, 10, 1);
        LocalDate dateEnd2 = LocalDate.of(2010, 10, 1);

        Company.Period period1 = new Company.Period(dateBegin1, dateEnd1, "Manager", "did smth");
        Company.Period period2 = new Company.Period(dateBegin1, dateEnd1, "Manager1", "did smth1");
        Company.Period period3 = new Company.Period(dateBegin2, dateEnd2, "Manager2", null);

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

        return r1;
    }
}
