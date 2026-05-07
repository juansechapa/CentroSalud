<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <title><fmt:message key="auditoria.titulo"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />
        <div class="container mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="section-title mb-0"><fmt:message key="auditoria.titulo"/></h2>
                <div class="d-flex gap-2">
                    <!-- Botones de exportación (solo para recepcionista) -->
                    <c:if test="${sessionScope.rol == 'RECEPCIONISTA'}">
                        <div class="btn-group" role="group">
                            <a href="${pageContext.request.contextPath}/exportar-logs?tipo=pdf" class="btn btn-danger" title="<fmt:message key="cita.descargar.pdf"/>">
                                <i class="bi bi-file-pdf"></i> PDF
                            </a>
                            <a href="${pageContext.request.contextPath}/exportar-logs?tipo=excel" class="btn btn-success" title="<fmt:message key="cita.descargar.excel"/>">
                                <i class="bi bi-file-excel"></i> Excel
                            </a>
                        </div>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/log-auditoria" class="btn btn-outline-primary btn-sm">
                        <i class="bi bi-arrow-repeat"></i> <fmt:message key="auditoria.actualizar"/>
                    </a>
                </div>
            </div>

            <!-- Barra de búsqueda client‑side -->
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
                    <i class="bi bi-shield-lock"></i> <fmt:message key="auditoria.lista.titulo"/>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0" id="logsTable">
                            <thead>
                                <tr>
                                    <th><fmt:message key="auditoria.id"/></th>
                                    <th><fmt:message key="auditoria.usuario"/></th>
                                    <th><fmt:message key="auditoria.username"/></th>
                                    <th><fmt:message key="auditoria.accion"/></th>
                                    <th><fmt:message key="auditoria.ip"/></th>
                                    <th><fmt:message key="auditoria.resultado"/></th>
                                    <th><fmt:message key="auditoria.fecha"/></th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="log" items="${logs}">
                                    <tr class="log-fila">
                                        <td data-buscar="${log.id}">${log.id}</td>
                                        <td data-buscar="${log.idUsuario} ${log.username}">${log.idUsuario} (${log.username})</td>
                                        <td data-buscar="${log.username}">${log.username}</td>
                                        <td data-buscar="${log.accion}">${log.accion}</td>
                                        <td data-buscar="${log.ip}">${log.ip}</td>
                                        <td data-buscar="${log.resultado}">
                                            <span class="badge ${log.resultado == 'EXITO' ? 'bg-success' : 'bg-danger'}">
                                                ${log.resultado}
                                            </span>
                                        </td>
                                        <td data-buscar="${log.fechaFormateada}">
                                            <c:choose>
                                                <c:when test="${log.fecha != null}">
                                                    ${log.fechaFormateada}
                                                </c:when>
                                                <c:otherwise>
                                                    <fmt:message key="auditoria.fecha.vacia"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty logs}">
                                    <tr>
                                        <td colspan="7" class="text-center text-muted">
                                            <fmt:message key="auditoria.lista.vacia"/>
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
        <script>
                                // Búsqueda client-side
                                function filtrarTabla() {
                                    const input = document.getElementById('searchInput');
                                    const filter = input.value.toLowerCase().trim();
                                    const table = document.getElementById('logsTable');
                                    const tbody = table.getElementsByTagName('tbody')[0];
                                    const rows = tbody.getElementsByTagName('tr');
                                    let anyVisible = false;

                                    for (let i = 0; i < rows.length; i++) {
                                        const row = rows[i];
                                        // Si es la fila de "no hay registros" (cuando logs viene vacío), la saltamos
                                        if (row.cells.length === 1 && row.cells[0].getAttribute('colspan') === '7')
                                            continue;

                                        let rowText = '';
                                        const cells = row.getElementsByTagName('td');
                                        for (let j = 0; j < cells.length; j++) {
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

                                    // Manejar mensaje de "no resultados"
                                    let noResultsRow = document.getElementById('noResultsRow');
                                    if (!anyVisible && rows.length > 0 && !(rows.length === 1 && rows[0].cells.length === 1 && rows[0].cells[0].getAttribute('colspan') === '7')) {
                                        if (!noResultsRow) {
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