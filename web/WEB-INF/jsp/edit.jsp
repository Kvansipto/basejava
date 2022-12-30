<%@ page import="com.urise.webapp.model.TextSection" %>
<%@ page import="com.urise.webapp.model.SectionType" %>
<%@ page import="com.urise.webapp.model.ContactType" %>
<%@ page import="com.urise.webapp.model.ListSection" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <link rel="stylesheet" href="css/style.css">
    <jsp:useBean id="resume" class="com.urise.webapp.model.Resume" scope="request"/>
    <title>Резюме ${resume.fullName}</title>
</head>
<body>
<jsp:include page="fragments/header.jsp"/>
<section>
    <form method="post" action="resume" enctype="application/x-www-form-urlencoded">
        <input type="hidden" name="uuid" value="${resume.uuid}">
        <dl>
            <dt>Имя:</dt>
            <dd><label>
                <input required type="text" name="fullName" size="50" value="${resume.fullName}">
            </label></dd>
        </dl>
        <h3>Контакты:</h3>
        <c:forEach var="type" items="<%=ContactType.values()%>">
            <dl>
                <dt>${type.title}</dt>
                <dd><label>
                    <input type="text" name="${type.name()}" size="30" value="${resume.getContact(type)}">
                </label></dd>
            </dl>
        </c:forEach>
        <hr>
        <c:forEach var="sectionEntry" items="<%=SectionType.values()%>">
            <c:set var="section" value="${resume.getSection(sectionEntry)}"/>
            <jsp:useBean id="section" class="com.urise.webapp.model.Section"/>
            <%--            <jsp:useBean id="list" type="com.urise.webapp.model.ListSection"/>--%>
            <c:choose>
                <c:when test="${sectionEntry=='OBJECTIVE'}">
                    <h2><a>${sectionEntry.title}</a></h2>
                    <input type="text" name="${sectionEntry}" size="90"
                           value="${section}">
                </c:when>
                <c:when test="${sectionEntry=='PERSONAL'}">
                    <h2><a>${sectionEntry.title}</a></h2>
                    <textarea name="${sectionEntry}" cols=75 rows=5>${section}</textarea>
                </c:when>
                <c:when test="${sectionEntry=='ACHIEVEMENT' || sectionEntry=='QUALIFICATIONS'}">
                    <h2><a>${sectionEntry.title}</a></h2>
                    <c:choose>
                        <c:when test="${section == null}">
                            <textarea name="${sectionEntry}" cols=75 rows=5></textarea>
                        </c:when>
                        <c:otherwise>
                    <textarea name="${sectionEntry}" cols=75
                              rows=5><%=String.join("\n", ((ListSection) section).getContent())%></textarea>
                        </c:otherwise>
                    </c:choose>
                </c:when>
            </c:choose>
        </c:forEach>
        <br/>
        <hr>
        <button type="submit">Сохранить</button>
        <button type="reset" onclick="window.history.back()">Отменить</button>
    </form>
</section>
<jsp:include page="fragments/footer.jsp"/>
</body>
</html>
