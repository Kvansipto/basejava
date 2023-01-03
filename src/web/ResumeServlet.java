package web;

import com.urise.webapp.Config;
import com.urise.webapp.exception.NotExistStorageException;
import com.urise.webapp.model.*;
import com.urise.webapp.storage.Storage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class ResumeServlet extends HttpServlet {

    private Storage storage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        storage = Config.get().getStorage();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String uuid = request.getParameter("uuid");
        String fullName = request.getParameter("fullName");
        Resume r = storage.get(uuid);
        r.setFullName(fullName);
        for (ContactType type : ContactType.values()) {
            String value = request.getParameter(type.name());
            if (value != null && value.trim().length() != 0) {
                r.addContact(type, value);
            } else {
                r.getContactMap().remove(type);
            }
        }
        for (SectionType sectionType : SectionType.values()) {
            String value = request.getParameter(sectionType.name());
            if (value != null && value.trim().length() != 0) {
                switch (sectionType) {
                    case PERSONAL, OBJECTIVE -> r.addSection(sectionType, new TextSection(value));
                    case QUALIFICATIONS, ACHIEVEMENT -> {
                        r.addSection(sectionType, new ListSection(
                                new ArrayList<>(Arrays.asList(value.split("\\r\\n")))));
                    }
                }
            } else if (sectionType != SectionType.EDUCATION && sectionType != SectionType.EXPERIENCE) {
                r.getSectionMap().remove(sectionType);
            }
        }
        storage.update(r);
        response.sendRedirect("resume");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws
            javax.servlet.ServletException, IOException {
        String uuid = request.getParameter("uuid");
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("resumes", storage.getAllSorted());
            request.getRequestDispatcher("/WEB-INF/jsp/list.jsp").forward(request, response);
            return;
        }
        Resume resume = null;
        switch (action) {
            case "delete" -> {
                storage.delete(uuid);
                response.sendRedirect("resume");
                return;
            }

            case "view" -> {
                resume = storage.get(uuid);
            }
            case "edit" -> {

                try {
                    resume = storage.get(uuid);
                } catch (NotExistStorageException e) {
                    resume = new Resume(uuid, "Your name");
                    storage.save(resume);
                } finally {
                    for (SectionType sectionType : SectionType.values()) {
                        if (Objects.requireNonNull(resume).getSection(sectionType) == null) {
                            switch (sectionType) {
                                case PERSONAL, OBJECTIVE -> resume.sectionMap.put(sectionType, new TextSection(""));
                                case QUALIFICATIONS, ACHIEVEMENT -> resume.sectionMap.put(sectionType, new ListSection(new ArrayList<>() {{
                                    add("");
                                }}));
                                case EXPERIENCE, EDUCATION -> resume.sectionMap.put(sectionType, new CompanySection(new ArrayList<>() {{
                                    add(new Company("", new ArrayList<>()));
                                }}));
                            }
                        }
                    }
                }
            }
            default -> throw new IllegalArgumentException("Action " + action + " is illegal");
        }
        request.setAttribute("resume", resume);
        request.getRequestDispatcher(
                ("view".equals(action) ? "/WEB-INF/jsp/view.jsp" : "/WEB-INF/jsp/edit.jsp")
        ).forward(request, response);
    }
}

