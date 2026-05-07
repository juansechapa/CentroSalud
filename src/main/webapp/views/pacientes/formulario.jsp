<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<!DOCTYPE html>
<html>
    <head>
        <title>
            <c:choose>
                <c:when test="${not empty paciente}">
                    <fmt:message key="paciente.editar"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="paciente.nuevo"/>
                </c:otherwise>
            </c:choose>
        </title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Inter:opsz,wght@14..32,300;400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>

        <jsp:include page="../templates/header.jsp" />

        <div class="container mt-4" style="max-width: 700px;">
            <div class="card">
                <div class="card-header-custom">
                    <i class="bi bi-person-plus-fill"></i>
                    <c:choose>
                        <c:when test="${not empty paciente}">
                            <fmt:message key="paciente.editar"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="paciente.nuevo"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/PacienteServlet" method="post">
                        <input type="hidden" name="accion" value="${not empty paciente ? 'actualizar' : 'insertar'}">
                        <c:if test="${not empty paciente}">
                            <input type="hidden" name="id" value="${paciente.id}">
                        </c:if>

                        <div class="row g-3">
                            <div class="col-md-6">
                                <label class="form-label"><fmt:message key="paciente.nombres"/> *</label>
                                <input type="text" name="nombres" class="form-control" value="${paciente.nombres}" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label"><fmt:message key="paciente.apellidos"/> *</label>
                                <input type="text" name="apellidos" class="form-control" value="${paciente.apellidos}" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label"><fmt:message key="paciente.documento"/> *</label>
                                <input type="text" name="documento" class="form-control" value="${paciente.documento}" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label"><fmt:message key="paciente.nacimiento"/></label>
                                <input type="date" name="fechaNacimiento" class="form-control" value="${paciente.fechaNacimiento}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label"><fmt:message key="paciente.telefono"/></label>
                                <input type="text" name="telefono" class="form-control" value="${paciente.telefono}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label"><fmt:message key="paciente.email"/></label>
                                <input type="email" name="email" class="form-control" value="${paciente.email}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label"><fmt:message key="paciente.eps"/></label>
                                <input type="text" name="eps" class="form-control" value="${paciente.eps}">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label"><fmt:message key="paciente.vereda_barrio"/></label>
                                <input type="text" name="veredaBarrio" class="form-control" value="${paciente.veredaBarrio}">
                            </div>
                            <div class="col-12">
                                <hr>
                                <button type="submit" class="btn btn-success"><i class="bi bi-save"></i> <fmt:message key="paciente.guardar"/></button>
                                <a href="${pageContext.request.contextPath}/PacienteServlet?accion=listar" class="btn btn-secondary"><fmt:message key="paciente.cancelar"/></a>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>