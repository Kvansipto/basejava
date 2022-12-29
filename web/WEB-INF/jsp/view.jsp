<%@ page import="com.urise.webapp.model.ContactType" %>
<%@ page import="web.HtmlUtil" %>
<%@ page import="java.util.Objects" %>
<%@ page import="com.urise.webapp.model.TextSection" %>
<%@ page import="com.urise.webapp.model.ListSection" %>
<%@ page import="com.urise.webapp.model.CompanySection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <jsp:useBean id="resume" type="com.urise.webapp.model.Resume" scope="request"/>
    <title>Резюме ${resume.fullName}</title>
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<section>
    <h2>${resume.fullName}&nbsp;<a href="resume?uuid=${resume.uuid}&action=edit"><img src="img/pencil.png"></a></h2>
    <p>
        <c:forEach var="contactEntry" items="${resume.contactMap}">
            <jsp:useBean id="contactEntry"
                         type="java.util.Map.Entry<com.urise.webapp.model.ContactType, java.lang.String>"/>
            <%=HtmlUtil.toHtml(contactEntry.getKey(), contactEntry.getValue())%><br/> </c:forEach>
    </p>
    <p>
        <c:forEach var="sectionEntry" items="${resume.sectionMap}">
            <jsp:useBean id="sectionEntry"
                         type="java.util.Map.Entry<com.urise.webapp.model.SectionType, com.urise.webapp.model.Section>"/>
            <c:set var="sectionType" value="${sectionEntry.key}"/>
            <c:set var="section" value="${sectionEntry.value}"/>
            <jsp:useBean id="section" type="com.urise.webapp.model.Section"/>
    <h3>${sectionType.title}</h3>
    <c:choose>
        <c:when test="${sectionType == 'PERSONAL' || sectionType == 'OBJECTIVE'}">
            <%=((TextSection) section).getContent()%>
        </c:when>
        <c:when test="${sectionType == 'ACHIEVEMENT' || sectionType == 'QUALIFICATIONS'}">
            <ul>
                <c:forEach var="content" items="<%=((ListSection)section).getContent()%>">
                    <il>${content}</il>
                    <br/>
                </c:forEach>
            </ul>
        </c:when>
        <c:when test="${sectionType == 'EXPERIENCE' || sectionType == 'EDUCATION'}">
            <table class="view-table" class="n">
                <c:forEach var="company" items="<%=((CompanySection)section).getCompanies()%>">
                    <c:forEach var="period" items="${company.periods}">
                        <jsp:useBean id="period" type="com.urise.webapp.model.Company.Period"/>
                        <tr class="n">
                            <td class="n"><%=HtmlUtil.toHtml(period)%>
                            </td>
                            <td class="n">${period.title}, ${company.name}</td>
                        </tr>
                        <tr class="n">
                            <td class="n"></td>
                            <td class="n">${period.description}</td>
                        </tr>
                    </c:forEach>
                </c:forEach>
            </table>
        </c:when>
    </c:choose>
    </c:forEach>
    </p>
</section>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>
