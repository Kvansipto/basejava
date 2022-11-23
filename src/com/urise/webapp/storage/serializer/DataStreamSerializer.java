package com.urise.webapp.storage.serializer;

import com.urise.webapp.model.*;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class DataStreamSerializer implements StreamSerializer {

    public <T> void writeWithException(Collection<T> collection, DataOutputStream dos, DataStreamWriteConsumer<T> consumer) throws IOException {

        dos.writeInt(collection.size());
        for (T v : collection) {
            consumer.accept(v);
        }
    }

    public <T> void readWithException(DataInputStream dis, DataStreamReadConsumer consumer) throws IOException {

        int size = dis.readInt();
        for (int i = 0; i < size; i++) {
            consumer.accept();
        }
    }

    public <T> List<T> addListWithException(DataStreamListConsumer<List<T>> consumer) throws IOException {

        List<T> t = new ArrayList<>();
        consumer.accept(t);
        return t;
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

            Resume resume = new Resume(dis.readUTF(), dis.readUTF());

            readWithException(dis, () -> resume.addContact(ContactType.valueOf(dis.readUTF()), dis.readUTF()));
            readWithException(dis, () -> {
                SectionType sectionType = SectionType.valueOf(dis.readUTF());

                switch (sectionType) {
                    case PERSONAL, OBJECTIVE -> resume.addSection(sectionType, new TextSection(dis.readUTF()));
                    case ACHIEVEMENT, QUALIFICATIONS -> resume.addSection(sectionType, new ListSection(addListWithException((t) -> readWithException(dis, () -> t.add(dis.readUTF())))));
                    case EXPERIENCE, EDUCATION -> resume.addSection(sectionType, new CompanySection(addListWithException(t -> readWithException(dis, () -> {
                        String companyName = dis.readUTF();
                        t.add(new Company(companyName, addListWithException(t1 -> readWithException(dis, () -> {
                            String desc = null;
                            if (dis.readBoolean()) desc = dis.readUTF();
                            t1.add(new Company.Period(readDate(dis), readDate(dis), dis.readUTF(), desc));
                        }))));
                    }))));
                }
            });
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
