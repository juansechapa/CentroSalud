<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<c:if test="${empty listaHorarios}">
    <c:redirect url="${pageContext.request.contextPath}/HorarioServlet?accion=listar" />
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <title><fmt:message key="horario.titulo"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />

        <div class="container mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="section-title mb-0"><fmt:message key="horario.lista.titulo"/></h2>
                <div class="d-flex gap-2">
                    <!-- Botones de exportación -->
                    <div class="btn-group" role="group">
                        <a href="${pageContext.request.contextPath}/exportar-horarios?tipo=pdf" class="btn btn-danger" title="<fmt:message key="cita.descargar.pdf"/>">
                            <i class="bi bi-file-pdf"></i> PDF
                        </a>
                        <a href="${pageContext.request.contextPath}/exportar-horarios?tipo=excel" class="btn btn-success" title="<fmt:message key="cita.descargar.excel"/>">
                            <i class="bi bi-file-excel"></i> Excel
                        </a>
                    </div>
                    <!-- Botón Nuevo Horario (solo para recepcionista) -->
                    <c:if test="${sessionScope.rol == 'RECEPCIONISTA'}">
                        <a href="${pageContext.request.contextPath}/HorarioServlet?accion=nuevo" class="btn btn-success">
                            <i class="bi bi-plus-circle"></i> <fmt:message key="horario.nuevo"/>
                        </a>
                    </c:if>
                </div>
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

            <!-- Barra de búsqueda client-side -->
            <div class="card mb-3">
                <div class="card-body py-2">
                    <div class="row g-2">
                        <div class="col-md-10">
                            <input type="text" id="searchInput" class="form-control" 
                                   placeholder="<fmt:message key="accion.buscar"/>..." 
                                   onkeyup="filtrarTabla()">
                        </div>
                        <div class="col-md-2">
                            <button class="btn btn-outline-secondary w-100" onclick="document.getElementById('searchInput').value = ''; filtrarTabla();">
                                <i class="bi bi-x-circle"></i> <fmt:message key="accion.limpiar"/>
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <div class="card">
                <div class="card-header-custom">
                    <i class="bi bi-clock-history me-2"></i> <fmt:message key="horario.lista.titulo"/>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0" id="horariosTable">
                            <thead>
                                <tr>
                                    <th><fmt:message key="horario.id"/></th>
                                    <th><fmt:message key="horario.medico.id"/></th>
                                    <th><fmt:message key="horario.dia"/></th>
                                    <th><fmt:message key="horario.hora_inicio"/></th>
                                    <th><fmt:message key="horario.hora_fin"/></th>
                                    <th><fmt:message key="horario.max_citas"/></th>
                                        <c:if test="${sessionScope.rol == 'RECEPCIONISTA'}">
                                        <th><fmt:message key="accion.acciones"/></th>
                                        </c:if>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="h" items="${listaHorarios}">
                                    <tr class="horario-fila">
                                        <td data-buscar="${h.id}">${h.id}</td>
                                        <td data-buscar="${h.idMedico}">${h.idMedico}</td>
                                        <td data-buscar="
                                            <c:choose>
                                                <c:when test="${h.diaSemana == 1}">Lunes</c:when>
                                                <c:when test="${h.diaSemana == 2}">Martes</c:when>
                                                <c:when test="${h.diaSemana == 3}">Miércoles</c:when>
                                                <c:when test="${h.diaSemana == 4}">Jueves</c:when>
                                                <c:when test="${h.diaSemana == 5}">Viernes</c:when>
                                                <c:when test="${h.diaSemana == 6}">Sábado</c:when>
                                                <c:when test="${h.diaSemana == 7}">Domingo</c:when>
                                                <c:otherwise>${h.diaSemana}</c:otherwise>
                                            </c:choose>
                                            ">
                                            <c:choose>
                                                <c:when test="${h.diaSemana == 1}"><fmt:message key="horario.dia.lunes"/></c:when>
                                                <c:when test="${h.diaSemana == 2}"><fmt:message key="horario.dia.martes"/></c:when>
                                                <c:when test="${h.diaSemana == 3}"><fmt:message key="horario.dia.miercoles"/></c:when>
                                                <c:when test="${h.diaSemana == 4}"><fmt:message key="horario.dia.jueves"/></c:when>
                                                <c:when test="${h.diaSemana == 5}"><fmt:message key="horario.dia.viernes"/></c:when>
                                                <c:when test="${h.diaSemana == 6}"><fmt:message key="horario.dia.sabado"/></c:when>
                                                <c:when test="${h.diaSemana == 7}"><fmt:message key="horario.dia.domingo"/></c:when>
                                                <c:otherwise>${h.diaSemana}</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td data-buscar="${h.horaInicio}">${h.horaInicio}</td>
                                        <td data-buscar="${h.horaFin}">${h.horaFin}</td>
                                        <td data-buscar="${h.maxCitas}">${h.maxCitas}</td>
                                        <c:if test="${sessionScope.rol == 'RECEPCIONISTA'}">
                                            <td class="text-nowrap">
                                                <a href="${pageContext.request.contextPath}/HorarioServlet?accion=editar&id=${h.id}" 
                                                   class="btn btn-sm btn-primary" title="<fmt:message key="accion.editar"/>">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal"
                                                        data-id="${h.id}" data-nombre="<fmt:message key="horario.medico.id"/> ${h.idMedico} - <fmt:message key="horario.dia"/> ${h.diaSemana}">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty listaHorarios}">
                                    <tr>
                                        <td colspan="${sessionScope.rol == 'RECEPCIONISTA' ? 7 : 6}" class="text-center text-muted">
                                            <fmt:message key="horario.lista.vacia"/>
                                        </td>
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
                        <h5 class="modal-title" id="deleteModalLabel">
                            <i class="bi bi-exclamation-triangle-fill"></i> <fmt:message key="horario.confirmar.eliminar"/>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p><fmt:message key="horario.confirmar.eliminar.pregunta"/></p>
                        <p class="fw-bold" id="deleteHorarioInfo"></p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <fmt:message key="accion.cancelar"/>
                        </button>
                        <a href="#" id="confirmDeleteBtn" class="btn btn-danger">
                            <fmt:message key="accion.eliminar"/>
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
                                // Filtrar tabla en tiempo real
                                function filtrarTabla() {
                                    const input = document.getElementById('searchInput');
                                    const filter = input.value.toLowerCase().trim();
                                    const table = document.getElementById('horariosTable');
                                    const rows = table.getElementsByTagName('tr');
                                    let anyVisible = false;

                                    for (let i = 1; i < rows.length; i++) { // i=1 para saltar el encabezado
                                        const row = rows[i];
                                        // Obtener todas las celdas de texto de la fila (excepto la última columna de acciones)
                                        const cells = row.getElementsByTagName('td');
                                        let rowText = '';
                                        for (let j = 0; j < cells.length; j++) {
                                            // Evitar incluir la columna de acciones (última o penúltima)
                                            // Si la celda tiene atributo data-buscar, lo usamos; si no, el texto visible
                                            const cellValue = cells[j].getAttribute('data-buscar') || cells[j].innerText;
                                            rowText += cellValue.toLowerCase() + ' ';
                                        }
                                        if (filter === '' || rowText.indexOf(filter) > -1) {
                                            row.style.display = '';
                                            anyVisible = true;
                                        } else {
                                            row.style.display = 'none';
                                        }
                                    }
                                    // Mostrar mensaje si no hay resultados visibles
                                    let noResultsRow = document.getElementById('noResultsRow');
                                    if (!anyVisible && rows.length > 1) {
                                        if (!noResultsRow) {
                                            const tbody = table.getElementsByTagName('tbody')[0];
                                            const newRow = tbody.insertRow();
                                            newRow.id = 'noResultsRow';
                                            const colspan = table.rows[0].cells.length;
                                            const cell = newRow.insertCell(0);
                                            cell.colSpan = colspan;
                                            cell.className = 'text-center text-muted';
                                            cell.innerHTML = '<fmt:message key="busqueda.sin.resultados"/>';
                                        }
                                    } else {
                                        if (noResultsRow)
                                            noResultsRow.remove();
                                    }
                                }
        </script>
    </body>
</html>