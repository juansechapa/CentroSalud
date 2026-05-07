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
        <title><fmt:message key="nav.citas"/> | Enfermería</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />
        <div class="container mt-4">
            <h2><fmt:message key="cita.lista.titulo"/></h2>
            <div class="card">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th><fmt:message key="cita.paciente"/></th>
                                    <th><fmt:message key="cita.medico"/></th>
                                    <th><fmt:message key="cita.fecha"/></th>
                                    <th><fmt:message key="cita.hora"/></th>
                                    <th><fmt:message key="cita.estado"/></th>
                                    <th><fmt:message key="cita.motivo"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${listaCitas}">
                                    <tr>
                                        <td>${c.nombrePaciente}</td>
                                        <td>${c.nombreMedico}</td>
                                        <td>${c.fechaCita}</td>
                                        <td>${c.horaCita}</td>
                                        <td>
                                            <span class="badge 
                                                  ${c.estado == 'PROGRAMADA' ? 'badge-programada' : 
                                                    (c.estado == 'CONFIRMADA' ? 'badge-confirmada' : 
                                                    (c.estado == 'ATENDIDA' ? 'badge-atendida' : 'badge-cancelada'))}">
                                                  <fmt:message key="cita.estado.${c.estado.toLowerCase()}"/>
                                            </span>
                                        </td>
                                        <td>${c.motivo}</td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty listaCitas}">
                                    <tr>
                                        <td colspan="6" class="text-center text-muted">
                                            <fmt:message key="cita.lista.vacia"/>
                                        </td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>