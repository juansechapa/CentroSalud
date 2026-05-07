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
                <c:when test="${not empty cita}">
                    <fmt:message key="cita.editar"/> | SaludBoyaca
                </c:when>
                <c:otherwise>
                    <fmt:message key="cita.nueva"/> | SaludBoyaca
                </c:otherwise>
            </c:choose>
        </title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill"></i> ${param.error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>
        <c:if test="${not empty param.mensaje}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill"></i> ${param.mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <div class="container mt-4" style="max-width: 800px;">
            <div class="card">
                <div class="card-header-custom">
                    <i class="bi bi-calendar-plus"></i> 
                    <c:choose>
                        <c:when test="${not empty cita}">
                            <fmt:message key="cita.editar"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="cita.nueva"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/CitaServlet" method="post">
                        <input type="hidden" name="accion" value="${not empty cita ? 'actualizar' : 'insertar'}">
                        <c:if test="${not empty cita}">
                            <input type="hidden" name="id" value="${cita.id}">
                        </c:if>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label"><fmt:message key="cita.paciente"/> *</label>
                                <select name="idPaciente" class="form-select" required>
                                    <option value=""><fmt:message key="cita.paciente.seleccionar"/></option>
                                    <c:forEach var="p" items="${pacientes}">
                                        <option value="${p.id}" ${cita.idPaciente == p.id ? 'selected' : ''}>${p.nombres} ${p.apellidos} (${p.documento})</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label"><fmt:message key="cita.medico"/> *</label>
                                <select name="idMedico" class="form-select" required>
                                    <option value=""><fmt:message key="cita.medico.seleccionar"/></option>
                                    <c:forEach var="m" items="${medicos}">
                                        <option value="${m.id}" ${cita.idMedico == m.id ? 'selected' : ''}>${m.nombres} ${m.apellidos} (${m.especialidad})</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label"><fmt:message key="cita.especialidad"/> *</label>
                                <select name="idEspecialidad" class="form-select" required>
                                    <option value=""><fmt:message key="cita.especialidad.seleccionar"/></option>
                                    <c:forEach var="e" items="${especialidades}">
                                        <option value="${e.id}" ${cita.idEspecialidad == e.id ? 'selected' : ''}>${e.nombre}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label"><fmt:message key="cita.fecha"/> *</label>
                                <input type="date" name="fechaCita" class="form-control" value="${cita.fechaCita}" required>
                            </div>
                            <div class="col-md-3 mb-3">
                                <label class="form-label"><fmt:message key="cita.hora"/> *</label>
                                <input type="time" name="horaCita" class="form-control" value="${cita.horaCita}" required>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label"><fmt:message key="cita.motivo"/></label>
                            <textarea name="motivo" class="form-control" rows="2">${cita.motivo}</textarea>
                        </div>
                        <div class="mb-3">
                            <label class="form-label"><fmt:message key="cita.estado"/></label>
                            <select name="estado" class="form-select" required>
                                <option value="PROGRAMADA" ${cita.estado == 'PROGRAMADA' ? 'selected' : ''}><fmt:message key="cita.estado.programada"/></option>
                                <option value="CONFIRMADA" ${cita.estado == 'CONFIRMADA' ? 'selected' : ''}><fmt:message key="cita.estado.confirmada"/></option>
                                <option value="ATENDIDA" ${cita.estado == 'ATENDIDA' ? 'selected' : ''}><fmt:message key="cita.estado.atendida"/></option>
                                <option value="CANCELADA" ${cita.estado == 'CANCELADA' ? 'selected' : ''}><fmt:message key="cita.estado.cancelada"/></option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label"><fmt:message key="cita.observaciones"/></label>
                            <textarea name="observaciones" class="form-control" rows="2">${cita.observaciones}</textarea>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-success"><i class="bi bi-save"></i> <fmt:message key="cita.guardar"/></button>
                            <a href="${pageContext.request.contextPath}/CitaServlet?accion=listar" class="btn btn-secondary"><fmt:message key="cita.cancelar"/></a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>