package com.urise.webapp.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serial;
import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Initial resume class
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Resume implements Comparable<Resume>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String uuid;
    private String fullName;

    public final Map<SectionType, Section> sectionMap = new EnumMap<>(SectionType.class);
    public final Map<ContactType, String> contactMap = new EnumMap<>(ContactType.class);


    public Resume() {
    }

    public Resume(String fullName) {
        this(UUID.randomUUID().toString(), fullName);
    }

    public Resume(String uuid, String fullName) {
        this.uuid = uuid;
        this.fullName = fullName;
    }

    public Map<SectionType, Section> getSectionMap() {
        return sectionMap;
    }

    public Map<ContactType, String> getContactMap() {
        return contactMap;
    }

    public void addContact(ContactType contactType, String contact) {
        this.contactMap.put(contactType, contact);
    }

    public void addSection(SectionType sectionType, Section section) {
        this.sectionMap.put(sectionType, section);
    }

    public String getUuid() {
        return uuid;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Resume resume = (Resume) o;
        return Objects.equals(uuid, resume.uuid) && Objects.equals(fullName, resume.fullName) && Objects.equals(sectionMap, resume.sectionMap) && Objects.equals(contactMap, resume.contactMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, fullName, sectionMap, contactMap);
    }

    @Override
    public String toString() {
        return uuid;
    }

    @Override
    public int compareTo(Resume o) {
        return uuid.compareTo(o.uuid);
    }
}
