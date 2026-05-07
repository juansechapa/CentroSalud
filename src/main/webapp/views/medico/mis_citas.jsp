<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<c:if test="${empty listaCitas}">
    <c:redirect url="${pageContext.request.contextPath}/CitaServlet?accion=listar" />
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <title><fmt:message key="nav.mis_citas"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />
        <div class="container mt-4">
            <h2><fmt:message key="nav.mis_citas"/></h2>
            <c:if test="${not empty param.mensaje}">
                <div class="alert alert-success">${param.mensaje}</div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger">${param.error}</div>
            </c:if>
            <div class="card">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th><fmt:message key="cita.paciente"/></th>
                                    <th><fmt:message key="cita.fecha"/></th>
                                    <th><fmt:message key="cita.hora"/></th>
                                    <th><fmt:message key="cita.motivo"/></th>
                                    <th><fmt:message key="cita.estado"/></th>
                                    <th><fmt:message key="accion.acciones"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${listaCitas}">
                                    <tr>
                                        <td>${c.nombrePaciente}</td>
                                        <td>${c.fechaCita}</td>
                                        <td>${c.horaCita}</td>
                                        <td>${c.motivo}</td>
                                        <td>
                                            <span class="badge 
                                                  ${c.estado == 'PROGRAMADA' ? 'badge-programada' : 
                                                    (c.estado == 'CONFIRMADA' ? 'badge-confirmada' : 
                                                    (c.estado == 'ATENDIDA' ? 'badge-atendida' : 'badge-cancelada'))}">
                                                  <fmt:message key="cita.estado.${c.estado.toLowerCase()}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${c.estado == 'PROGRAMADA'}">
                                                    <a href="${pageContext.request.contextPath}/CitaServlet?accion=cambiarEstado&id=${c.id}&estado=CONFIRMADA" class="btn btn-sm btn-success">
                                                        <fmt:message key="accion.confirmar"/>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/CitaServlet?accion=cambiarEstado&id=${c.id}&estado=CANCELADA" class="btn btn-sm btn-danger">
                                                        <fmt:message key="accion.cancelar"/>
                                                    </a>
                                                </c:when>
                                                <c:when test="${c.estado == 'CONFIRMADA'}">
                                                    <a href="${pageContext.request.contextPath}/CitaServlet?accion=cambiarEstado&id=${c.id}&estado=ATENDIDA" class="btn btn-sm btn-info">
                                                        <fmt:message key="accion.atender"/>
                                                    </a>
                                                    <a href="${pageContext.request.contextPath}/CitaServlet?accion=cambiarEstado&id=${c.id}&estado=CANCELADA" class="btn btn-sm btn-danger">
                                                        <fmt:message key="accion.cancelar"/>
                                                    </a>
                                                </c:when>
                                                <c:when test="${c.estado == 'ATENDIDA'}">
                                                    <span class="text-muted"><fmt:message key="accion.atendida"/></span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="text-muted"><fmt:message key="accion.cancelada"/></span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
            <div class="mt-3">
                <a href="${pageContext.request.contextPath}/CitaServlet?accion=exportar" class="btn btn-success">
                    <i class="bi bi-file-pdf"></i> <fmt:message key="cita.descargar.pdf"/>
                </a>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>