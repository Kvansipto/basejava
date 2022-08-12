package com.urise.webapp;

import com.urise.webapp.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

public class ResumeTestData {
    public static void main(String[] args) {
        Resume r1 = new Resume("uuid5", "A");

        r1.contactMap.put(ContactType.EMAIL, "email");
        r1.contactMap.put(ContactType.PHONE, "+72873158212");
        r1.contactMap.put(ContactType.Skype, "dwbydu21");
        r1.contactMap.put(ContactType.GITHUB, "www.github.com");

        SectionContent personal = new SectionContent("personal content");
        SectionContent objective = new SectionContent("objective content");

        SectionContentList achievement = new SectionContentList(
                new ArrayList<>(Arrays.asList("first achieve", "second achieve", "third achieve")));

        SectionContentList qualifications = new SectionContentList(
                new ArrayList<>(Arrays.asList("first qualify", "second qualify", "third qualify")));

        Company company1 = new Company(new Date(1999, 01, 01), (new Date(2006, 01, 01)), "Company1", "Manager", "did smth");
        Company company2 = new Company(new Date(2007, 01, 01), (new Date(2009, 01, 01)), "Company2", "Manager2", "did smth2");

        CompanySection exprience = new CompanySection(Arrays.asList(company1, company2));

        CompanySection education = new CompanySection(Arrays.asList(company1, company2));

        r1.sectionMap.put(SectionType.PERSONAL, personal);
        r1.sectionMap.put(SectionType.OBJECTIVE, objective);
        r1.sectionMap.put(SectionType.ACHIEVEMENT, achievement);
        r1.sectionMap.put(SectionType.QUALIFICATIONS, qualifications);
        r1.sectionMap.put(SectionType.EXPERIENCE, exprience);
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
