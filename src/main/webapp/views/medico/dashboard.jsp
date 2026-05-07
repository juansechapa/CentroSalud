<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages_en" scope="session" />

<!DOCTYPE html>
<html>
    <head>
        <title><fmt:message key="nav.medico.panel"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />
        <div class="container mt-4">
            <h2>
                <fmt:message key="dashboard.medico.bienvenida">
                    <fmt:param value="${sessionScope.usuario.nombres}" />
                    <fmt:param value="${sessionScope.usuario.apellidos}" />
                </fmt:message>
            </h2>
            <div class="row mt-4">
                <div class="col-md-6">
                    <div class="card text-center p-3">
                        <i class="bi bi-calendar-check fs-1"></i>
                        <h4><fmt:message key="nav.mis_citas"/></h4>
                        <a href="${pageContext.request.contextPath}/CitaServlet?accion=listar" class="btn btn-primary">
                            <fmt:message key="accion.ver.citas"/>
                        </a>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="card text-center p-3">
                        <i class="bi bi-file-pdf fs-1"></i>
                        <h4><fmt:message key="cita.descargar.pdf"/></h4>
                        <a href="${pageContext.request.contextPath}/CitaServlet?accion=exportar" class="btn btn-success">
                            <fmt:message key="accion.descargar"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>