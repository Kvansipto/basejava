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
                switch (entry.getKey()) {
                    case PERSONAL, OBJECTIVE -> dos.writeUTF(entry.getValue().toString());

                    case ACHIEVEMENT, QUALIFICATIONS -> {
                        ListSection listSection = (ListSection) entry.getValue();
                        List<String> content1 = listSection.getContent();
                        dos.writeInt(content1.size());
                        for (String content : listSection.getContent()) {
                            dos.writeUTF(content);
                        }
                    }

                    case EXPERIENCE, EDUCATION -> {
                        CompanySection companySection = (CompanySection) entry.getValue();
                        List<Company> companies = companySection.getCompanies();

                        dos.writeInt(companies.size());
                        for (Company company : companies) {
                            dos.writeUTF(company.getName());
                            ArrayList<Company.Period> periods = company.getPeriods();

                            dos.writeInt(periods.size());
                            for (Company.Period period : periods) {

                                boolean descriptionIsExist = period.getDescription() != null;

                                dos.writeBoolean(descriptionIsExist);

                                writeDate(period.getDateBegin(), dos);
                                writeDate(period.getDateEnd(), dos);

                                dos.writeUTF(period.getTitle());

                                if (descriptionIsExist) dos.writeUTF(period.getDescription());
                            }
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
                switch (sectionType) {
                    case PERSONAL, OBJECTIVE -> {
                        resume.addSection(sectionType, new TextSection(dis.readUTF()));
                    }
                    case ACHIEVEMENT, QUALIFICATIONS -> {
                        List<String> list = new ArrayList<>();
                        int sectionValueSize = dis.readInt();
                        for (int x = 0; x < sectionValueSize; x++) {
                            list.add(dis.readUTF());
                        }
                        resume.addSection(sectionType, new ListSection(list));
                    }
                    case EXPERIENCE, EDUCATION -> {
                        int companySize = dis.readInt();
                        List<Company> companies = new ArrayList<>();
                        for (int x = 0; x < companySize; x++) {
                            String companyName = dis.readUTF();

                            int periodSize = dis.readInt();
                            ArrayList<Company.Period> periods = new ArrayList<>();
                            for (int y = 0; y < periodSize; y++) {
                                if (dis.readBoolean()) {
                                    periods.add(new Company.Period(readDate(dis), readDate(dis), dis.readUTF(), dis.readUTF()));
                                } else
                                    periods.add(new Company.Period(readDate(dis), readDate(dis), dis.readUTF(), null));
                            }
                            companies.add(new Company(companyName, periods));
                        }
                        resume.addSection(sectionType, new CompanySection(companies));
                    }
                }
            }
            return resume;
        }
    }

    private void writeDate(LocalDate localDate, DataOutputStream dos) throws IOException {
        dos.writeInt(localDate.getYear());
        dos.writeInt(localDate.getMonthValue());
        dos.writeInt(localDate.getDayOfMonth());

    }

    private LocalDate readDate(DataInputStream dis) throws IOException {
        return LocalDate.of(dis.readInt(), dis.readInt(), dis.readInt());
    }
}
