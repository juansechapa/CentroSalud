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
        <title><fmt:message key="cita.titulo"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />
        <div class="container mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="section-title mb-0"><fmt:message key="cita.lista.titulo"/></h2>
                <a href="${pageContext.request.contextPath}/CitaServlet?accion=nuevo" class="btn btn-success">
                    <i class="bi bi-plus-circle"></i> <fmt:message key="cita.nueva"/>
                </a>
            </div>

            <div class="btn-group" role="group">
                <a href="${pageContext.request.contextPath}/exportar-citas?tipo=pdf" class="btn btn-danger">
                    <i class="bi bi-file-pdf"></i> <fmt:message key="cita.descargar.pdf"/>
                </a>
                <a href="${pageContext.request.contextPath}/exportar-citas?tipo=excel" class="btn btn-success">
                    <i class="bi bi-file-excel"></i> <fmt:message key="cita.descargar.excel"/>
                </a>
            </div>

            <c:if test="${not empty param.mensaje}">
                <div class="alert alert-exito alert-dismissible fade show">
                    ${param.mensaje}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-error alert-dismissible fade show">
                    ${param.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Filtros -->
            <div class="card mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/CitaServlet" class="row g-3">
                        <input type="hidden" name="accion" value="listar">
                        <div class="col-md-3">
                            <label class="form-label"><fmt:message key="cita.paciente.filtro"/></label>
                            <input type="text" name="buscarPaciente" class="form-control" value="${param.buscarPaciente}" placeholder="<fmt:message key="cita.paciente.buscar"/>">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label"><fmt:message key="cita.medico"/></label>
                            <select name="idMedico" class="form-select">
                                <option value=""><fmt:message key="cita.filtro.todos"/></option>
                                <c:forEach var="m" items="${medicos}">
                                    <option value="${m.id}" ${param.idMedico == m.id ? 'selected' : ''}>${m.nombres} ${m.apellidos}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label"><fmt:message key="cita.estado"/></label>
                            <select name="estado" class="form-select">
                                <option value=""><fmt:message key="cita.filtro.todos"/></option>
                                <c:forEach var="e" items="${estados}">
                                    <option value="${e}" ${param.estado == e ? 'selected' : ''}>${e}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <label class="form-label"><fmt:message key="cita.fecha.desde"/></label>
                            <input type="date" name="fechaInicio" class="form-control" value="${param.fechaInicio}">
                        </div>
                        <div class="col-md-2">
                            <label class="form-label"><fmt:message key="cita.fecha.hasta"/></label>
                            <input type="date" name="fechaFin" class="form-control" value="${param.fechaFin}">
                        </div>
                        <div class="col-md-1 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary w-100">
                                <i class="bi bi-search"></i> <fmt:message key="cita.filtro.buscar"/>
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Tabla -->
            <div class="card">
                <div class="card-header-custom">
                    <i class="bi bi-calendar-check"></i> <fmt:message key="cita.lista.titulo"/>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th><fmt:message key="cita.id"/></th>
                                    <th><fmt:message key="cita.paciente"/></th>
                                    <th><fmt:message key="cita.medico"/></th>
                                    <th><fmt:message key="cita.especialidad"/></th>
                                    <th><fmt:message key="cita.fecha"/></th>
                                    <th><fmt:message key="cita.hora"/></th>
                                    <th><fmt:message key="cita.estado"/></th>
                                    <th><fmt:message key="accion.acciones"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${listaCitas}">
                                    <tr>
                                        <td>${c.id}</td>
                                        <td>${c.nombrePaciente}</td>
                                        <td>${c.nombreMedico}</td>
                                        <td>${c.nombreEspecialidad}</td>
                                        <td>${c.fechaCita}</td>
                                        <td>${c.horaCita}</td>
                                        <td>
                                            <span class="badge 
                                                  ${c.estado == 'PROGRAMADA' ? 'badge-programada' : ''}
                                                  ${c.estado == 'CONFIRMADA' ? 'badge-confirmada' : ''}
                                                  ${c.estado == 'ATENDIDA' ? 'badge-atendida' : ''}
                                                  ${c.estado == 'CANCELADA' ? 'badge-cancelada' : ''}">
                                                <fmt:message key="cita.estado.${c.estado.toLowerCase()}"/>
                                            </span>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/CitaServlet?accion=editar&id=${c.id}" class="btn btn-sm btn-primary" title="<fmt:message key="accion.editar"/>">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/CitaServlet?accion=eliminar&id=${c.id}" class="btn btn-sm btn-danger" onclick="return confirm('<fmt:message key="cita.confirmar.eliminar"/>')" title="<fmt:message key="accion.eliminar"/>">
                                                <i class="bi bi-trash"></i>
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty listaCitas}">
                                    <tr>
                                        <td colspan="8" class="text-center text-muted">
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