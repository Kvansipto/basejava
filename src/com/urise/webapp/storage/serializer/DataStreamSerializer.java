package com.urise.webapp.storage.serializer;

import com.urise.webapp.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataStreamSerializer implements StreamSerializer {

    public void doWrite(Resume r, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());
            Map<ContactType, String> contacts = r.getContactMap();
            dos.writeInt(contacts.size());
            for (Map.Entry<ContactType, String> entry : contacts.entrySet()) {
                dos.writeUTF(entry.getKey().name());
                dos.writeUTF(entry.getValue());
            }

            Map<SectionType, Section> sections = r.getSectionMap();
            dos.writeInt(sections.size());
            for (Map.Entry<SectionType, Section> entry : sections.entrySet()) {
                dos.writeUTF(entry.getKey().name());
                if (entry.getKey() == SectionType.PERSONAL || entry.getKey() == SectionType.OBJECTIVE) {
                    dos.writeUTF(entry.getValue().toString());
                } else if (entry.getKey() == SectionType.ACHIEVEMENT || entry.getKey() == SectionType.QUALIFICATIONS) {
                    ListSection listSection = (ListSection) entry.getValue();
                    dos.writeInt(listSection.getContent().size());
                    for (String content : listSection.getContent()) {
                        dos.writeUTF(content);
                    }
                } else if (entry.getKey() == SectionType.EDUCATION || entry.getKey() == SectionType.EXPERIENCE) {
                    CompanySection companySection = (CompanySection) entry.getValue();
                    List<Company> companies = companySection.getCompanies();
                    dos.writeInt(companies.size());
                    for (Company company : companies) {
                        dos.writeUTF(company.getName());
                        ArrayList<Company.Period> periods = company.getPeriods();
                        dos.writeInt(periods.size());
                        for (Company.Period period : periods) {
                            dos.writeUTF(period.getDateBegin().toString());
                            dos.writeUTF(period.getDateEnd().toString());
                            dos.writeUTF(period.getTitle());
                            dos.writeUTF(period.getDescription());
                        }
                    }
                }
            }
        }
    }

    public Resume doRead(InputStream is) throws IOException {
        try (DataInputStream dis = new DataInputStream(is)) {
            String uuid = dis.readUTF();
            String fullName = dis.readUTF();
            Resume resume = new Resume(uuid, fullName);
            int size = dis.readInt();
            for (int i = 0; i < size; i++) {
                resume.addContact(ContactType.valueOf(dis.readUTF()), dis.readUTF());
            }
            size = dis.readInt();
            for (int i = 0; i < size; i++) {
                SectionType sectionType = SectionType.valueOf(dis.readUTF());
                if (sectionType == SectionType.PERSONAL || sectionType == SectionType.OBJECTIVE) {
                    resume.addSection(sectionType, new TextSection(dis.readUTF()));
                } else if (sectionType == SectionType.ACHIEVEMENT || sectionType == SectionType.QUALIFICATIONS) {
                    List<String> list = new ArrayList<>();
                    int sectionValueSize = dis.readInt();
                    for (int x = 0; x < sectionValueSize; x++) {
                        list.add(dis.readUTF());
                    }
                    resume.addSection(sectionType, new ListSection(list));
                } else if (sectionType == SectionType.EDUCATION || sectionType == SectionType.EXPERIENCE) {
                    int companySize = dis.readInt();
                    List<Company> companies = new ArrayList<>();
                    for (int x = 0; x < companySize; x++) {
                        String companyName = dis.readUTF();
                        int periodSize = dis.readInt();
                        ArrayList<Company.Period> periods = new ArrayList<>();
                        for (int y = 0; y < periodSize; y++) {
                            periods.add(new Company.Period(LocalDate.parse(dis.readUTF()), LocalDate.parse(dis.readUTF()), dis.readUTF(), dis.readUTF()));
                        }
                        companies.add(new Company(companyName, periods));
                    }
                    resume.addSection(sectionType, new CompanySection(companies));
                }
            }
            return resume;
        }
    }
}
