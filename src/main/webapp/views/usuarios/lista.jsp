<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<c:if test="${empty listaUsuarios}">
    <c:redirect url="${pageContext.request.contextPath}/UsuarioServlet?accion=listar" />
</c:if>

<!DOCTYPE html>
<html>
    <head>
        <title><fmt:message key="empleado.titulo"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />
        <div class="container mt-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="section-title mb-0"><fmt:message key="empleado.lista.titulo"/></h2>
                <div class="d-flex gap-2">
                    <!-- Botones de exportación -->
                    <div class="btn-group" role="group">
                        <a href="${pageContext.request.contextPath}/exportar-usuarios?tipo=pdf" class="btn btn-danger" title="<fmt:message key="cita.descargar.pdf"/>">
                            <i class="bi bi-file-pdf"></i> PDF
                        </a>
                        <a href="${pageContext.request.contextPath}/exportar-usuarios?tipo=excel" class="btn btn-success" title="<fmt:message key="cita.descargar.excel"/>">
                            <i class="bi bi-file-excel"></i> Excel
                        </a>
                    </div>
                    <!-- Botón Nuevo Empleado (solo para recepcionista) -->
                    <c:if test="${sessionScope.rol == 'RECEPCIONISTA'}">
                        <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=nuevo" class="btn btn-success">
                            <i class="bi bi-person-plus"></i> <fmt:message key="empleado.nuevo"/>
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
                    <i class="bi bi-people-fill"></i> <fmt:message key="empleado.lista.titulo"/>
                </div>
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0" id="empleadosTable">
                            <thead>
                                <tr>
                                    <th><fmt:message key="empleado.id"/></th>
                                    <th><fmt:message key="empleado.username"/></th>
                                    <th><fmt:message key="empleado.nombres"/></th>
                                    <th><fmt:message key="empleado.documento"/></th>
                                    <th><fmt:message key="empleado.rol"/></th>
                                    <th><fmt:message key="empleado.especialidad"/></th>
                                    <th><fmt:message key="empleado.activo"/></th>
                                        <c:if test="${sessionScope.rol == 'RECEPCIONISTA'}">
                                        <th><fmt:message key="accion.acciones"/></th>
                                        </c:if>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="u" items="${listaUsuarios}">
                                    <tr class="empleado-fila">
                                        <td data-buscar="${u.id}">${u.id}</td>
                                        <td data-buscar="${u.username}">${u.username}</td>
                                        <td data-buscar="${u.nombres} ${u.apellidos}">${u.nombres} ${u.apellidos}</td>
                                        <td data-buscar="${u.documento}">${u.documento}</td>
                                        <td data-buscar="${u.rol}">${u.rol}</td>
                                        <td data-buscar="${u.especialidad}">${u.especialidad}</td>
                                        <td data-buscar="${u.activo ? 'activo' : 'inactivo'}">
                                            <span class="badge ${u.activo ? 'bg-success' : 'bg-secondary'}">
                                                <fmt:message key="empleado.estado.${u.activo ? 'activo' : 'inactivo'}"/>
                                            </span>
                                        </td>
                                        <c:if test="${sessionScope.rol == 'RECEPCIONISTA'}">
                                            <td class="text-nowrap">
                                                <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=editar&id=${u.id}" 
                                                   class="btn btn-sm btn-primary" title="<fmt:message key="accion.editar"/>">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                                <button type="button" class="btn btn-sm btn-danger" data-bs-toggle="modal" data-bs-target="#deleteModal"
                                                        data-id="${u.id}" data-nombre="${u.nombres} ${u.apellidos}">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty listaUsuarios}">
                                    <tr>
                                        <td colspan="${sessionScope.rol == 'RECEPCIONISTA' ? 8 : 7}" class="text-center text-muted">
                                            <fmt:message key="empleado.lista.vacia"/>
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
                            <i class="bi bi-exclamation-triangle-fill"></i> <fmt:message key="empleado.confirmar.eliminar"/>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p><fmt:message key="empleado.confirmar.eliminar.pregunta"/></p>
                        <p class="fw-bold" id="deleteEmployeeName"></p>
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
                                // Eliminación modal
                                const deleteModal = document.getElementById('deleteModal');
                                if (deleteModal) {
                                    deleteModal.addEventListener('show.bs.modal', function (event) {
                                        const button = event.relatedTarget;
                                        const employeeId = button.getAttribute('data-id');
                                        const employeeName = button.getAttribute('data-nombre');
                                        const confirmBtn = document.getElementById('confirmDeleteBtn');
                                        const nameSpan = document.getElementById('deleteEmployeeName');
                                        nameSpan.textContent = employeeName;
                                        confirmBtn.href = '${pageContext.request.contextPath}/UsuarioServlet?accion=eliminar&id=' + employeeId;
                                    });
                                }

                                // Búsqueda client-side
                                function filtrarTabla() {
                                    const input = document.getElementById('searchInput');
                                    const filter = input.value.toLowerCase().trim();
                                    const table = document.getElementById('empleadosTable');
                                    const rows = table.getElementsByTagName('tbody')[0].getElementsByTagName('tr');
                                    let anyVisible = false;

                                    // Recorrer todas las filas del cuerpo (excluyendo posible fila de "no hay resultados")
                                    for (let i = 0; i < rows.length; i++) {
                                        const row = rows[i];
                                        // Si la fila es la del "no hay resultados" la ignoramos en este bucle
                                        if (row.id === 'noResultsRow')
                                            continue;
                                        let rowText = '';
                                        // Recoger todas las celdas de la fila (excepto la última columna de acciones)
                                        const cells = row.getElementsByTagName('td');
                                        for (let j = 0; j < cells.length; j++) {
                                            // Si celda tiene data-buscar, lo usamos; si no, el texto visible
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

                                    // Mostrar mensaje de no resultados si es necesario
                                    let noResultsRow = document.getElementById('noResultsRow');
                                    if (!anyVisible && rows.length > 0) {
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