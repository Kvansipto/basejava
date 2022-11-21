package com.urise.webapp.storage.serializer;

import com.urise.webapp.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DataStreamSerializer implements StreamSerializer {

    public <T> void writeWithException(Collection<T> collection, DataOutputStream dos, DataStreamCollectionConsumer<T> consumer) throws IOException {

        dos.writeInt(collection.size());
        for (T v : collection) {
            consumer.write(v);
        }
    }

    public void doWrite(Resume r, OutputStream os) throws IOException {
        try (DataOutputStream dos = new DataOutputStream(os)) {
            dos.writeUTF(r.getUuid());
            dos.writeUTF(r.getFullName());

            writeWithException(r.getContactMap().entrySet(), dos, t1 -> {
                dos.writeUTF(t1.getKey().name());
                dos.writeUTF(t1.getValue());
            });

            writeWithException(r.getSectionMap().entrySet(), dos, t -> {
                dos.writeUTF(t.getKey().name());
                switch (t.getKey()) {
                    case PERSONAL, OBJECTIVE -> dos.writeUTF(String.valueOf(t.getValue()));
                    case ACHIEVEMENT, QUALIFICATIONS -> writeWithException(((ListSection) t.getValue()).getContent(), dos, v -> dos.writeUTF(String.valueOf(v)));
                    case EXPERIENCE, EDUCATION -> writeWithException(((CompanySection) t.getValue()).getCompanies(), dos, c -> {
                        dos.writeUTF(c.getName());
                        writeWithException(c.getPeriods(), dos, p -> {
                            boolean descriptionIsExist = p.getDescription() != null;
                            dos.writeBoolean(descriptionIsExist);
                            if (descriptionIsExist) dos.writeUTF(p.getDescription());
                            writeDate(p.getDateBegin(), dos);
                            writeDate(p.getDateEnd(), dos);
                            dos.writeUTF(p.getTitle());
                        });
                    });
                }
            });
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
                                String desc = null;
                                if (dis.readBoolean()) {
                                    desc = dis.readUTF();
                                }
                                periods.add(new Company.Period(readDate(dis), readDate(dis), dis.readUTF(), desc));
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
