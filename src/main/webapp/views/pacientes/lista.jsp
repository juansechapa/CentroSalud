<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<!DOCTYPE html>
<html>
    <head>
        <title><fmt:message key="paciente.titulo"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />
        <div class="container mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="section-title mb-0"><fmt:message key="paciente.titulo"/></h2>
                <div class="d-flex gap-2">
                    <!-- Botones de exportación -->
                    <div class="btn-group" role="group">
                        <a href="${pageContext.request.contextPath}/exportar-pacientes?tipo=pdf" class="btn btn-danger" title="<fmt:message key="cita.descargar.pdf"/>">
                            <i class="bi bi-file-pdf"></i> PDF
                        </a>
                        <a href="${pageContext.request.contextPath}/exportar-pacientes?tipo=excel" class="btn btn-success" title="<fmt:message key="cita.descargar.excel"/>">
                            <i class="bi bi-file-excel"></i> Excel
                        </a>
                    </div>
                    <!-- Botón Nuevo Paciente (solo para recepcionista) -->
                    <c:if test="${sessionScope.rol == 'RECEPCIONISTA'}">
                        <a href="${pageContext.request.contextPath}/PacienteServlet?accion=nuevo" class="btn btn-success">
                            <i class="bi bi-plus-circle"></i> <fmt:message key="paciente.nuevo"/>
                        </a>
                    </c:if>
                </div>
            </div>

            <!-- Mensajes de éxito/error -->
            <c:if test="${not empty param.mensaje}">
                <div class="alert alert-success alert-dismissible fade show" role="alert">
                    <i class="bi bi-check-circle-fill"></i> ${param.mensaje}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="bi bi-exclamation-triangle-fill"></i> ${param.error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <!-- Búsqueda -->
            <div class="card mb-4">
                <div class="card-body">
                    <form method="get" action="${pageContext.request.contextPath}/PacienteServlet" class="row g-3">
                        <input type="hidden" name="accion" value="listar">
                        <div class="col-md-10">
                            <input type="text" name="buscar" class="form-control" placeholder="<fmt:message key="paciente.buscar.placeholder"/>" value="${param.buscar}">
                        </div>
                        <div class="col-md-2">
                            <button type="submit" class="btn btn-primary w-100"><i class="bi bi-search"></i> <fmt:message key="accion.buscar"/></button>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Tabla -->
            <div class="card">
                <div class="card-header-custom">
                    <i class="bi bi-people-fill me-2"></i> <fmt:message key="paciente.lista.titulo"/>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead>
                                <tr>
                                    <th><fmt:message key="paciente.id"/></th>
                                    <th><fmt:message key="paciente.documento"/></th>
                                    <th><fmt:message key="paciente.nombres"/></th>
                                    <th><fmt:message key="paciente.apellidos"/></th>
                                    <th><fmt:message key="paciente.telefono"/></th>
                                    <th><fmt:message key="paciente.email"/></th>
                                    <th><fmt:message key="paciente.eps"/></th>
                                    <th><fmt:message key="accion.acciones"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="p" items="${listaPacientes}">
                                    <tr>
                                        <td>${p.id}</td>
                                        <td>${p.documento}</td>
                                        <td>${p.nombres}</td>
                                        <td>${p.apellidos}</td>
                                        <td>${p.telefono}</td>
                                        <td>${p.email}</td>
                                        <td>${p.eps}</td>
                                        <td class="text-nowrap">
                                            <a href="${pageContext.request.contextPath}/PacienteServlet?accion=editar&id=${p.id}" class="btn btn-sm btn-primary" title="<fmt:message key="accion.editar"/>">
                                                <i class="bi bi-pencil"></i>
                                            </a>
                                            <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal" 
                                                    data-id="${p.id}" data-nombre="${p.nombres} ${p.apellidos}" title="<fmt:message key="accion.eliminar"/>">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty listaPacientes}">
                                    <tr>
                                        <td colspan="8" class="text-center text-muted"><fmt:message key="paciente.lista.vacia"/></td>
                                    </tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal de confirmación de eliminación -->
        <div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="deleteModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title" id="deleteModalLabel"><i class="bi bi-exclamation-triangle-fill"></i> <fmt:message key="accion.confirmar.eliminar"/></h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p><fmt:message key="paciente.confirmar.eliminar.pregunta"/></p>
                        <p class="fw-bold" id="deletePatientName"></p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal"><fmt:message key="accion.cancelar"/></button>
                        <a href="#" id="confirmDeleteBtn" class="btn btn-danger"><fmt:message key="accion.eliminar"/></a>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Manejar el modal de eliminación
            const deleteModal = document.getElementById('deleteModal');
            if (deleteModal) {
                deleteModal.addEventListener('show.bs.modal', function (event) {
                    const button = event.relatedTarget;
                    const pacienteId = button.getAttribute('data-id');
                    const pacienteNombre = button.getAttribute('data-nombre');
                    const confirmBtn = document.getElementById('confirmDeleteBtn');
                    const patientNameSpan = document.getElementById('deletePatientName');
                    patientNameSpan.textContent = pacienteNombre;
                    confirmBtn.href = '${pageContext.request.contextPath}/PacienteServlet?accion=eliminar&id=' + pacienteId;
                });
            }
        </script>
    </body>
</html>