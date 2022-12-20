package web;

import com.urise.webapp.Config;
import com.urise.webapp.model.Resume;
import com.urise.webapp.storage.Storage;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

public class ResumeServlet extends HttpServlet {

    private Storage storage;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        storage = Config.get().getStorage();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        String uuid = request.getParameter("uuid");
        Writer writer = response.getWriter();

        if (uuid == null) {
            writer.write(
                    "<html>\n" +
                            "<head>\n" +
                            "   <meta charset=\"utf-8\">\n" +
                            "   <link rel = \"stylesheet\" href=\"css/style.css\">\n" +
                            "   <title>Список всех резюме</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "   <h2 class=\"text\">Список всех резюме</h2>\n" +
                            "   <table border=\"1\">\n" +
                            "       <tr>\n" +
                            "           <th>Uuid</th>\n" +
                            "           <th>Full name</th>\n" +
                            "       </tr>\n");
            for (Resume resume : storage.getAllSorted()) {
                writer.write("      <tr>\n" +
                        "           <td>" + resume.getUuid() + "</td>\n" +
                        "           <td>" + resume.getFullName() + "</td>\n" +
                        "       </tr>\n");
            }
            writer.write("   </table>\n" +
                    "</body>\n" +
                    "</html>\n");
        } else {
            uuid = storage.get(uuid).getUuid();
            String fullName = storage.get(uuid).getFullName();
            writer.write(
                    "<html>\n" +
                            "<head>\n" +
                            "   <meta charset=\"utf-8\">\n" +
                            "   <link rel = \"stylesheet\" href=\"css/style.css\">\n" +
                            "   <title>Резюме" + uuid + "</title>\n" +
                            "</head>\n" +
                            "<body>\n" +
                            "   <h2 class=\"text\">Резюме\040" + uuid + "</h2>\n" +
                            "   <table border=\"1\">\n" +
                            "       <tr>\n" +
                            "           <th>Uuid</th>\n" +
                            "           <th>Full name</th>\n" +
                            "       </tr>\n" +
                            "       <tr>\n" +
                            "           <td>" + uuid + "</td>\n" +
                            "           <td>" + fullName + "</td>\n" +
                            "       </tr>\n" +
                            "   </table>\n" +
                            "</body>\n" +
                            "</html>");
        }
    }
}
