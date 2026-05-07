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
                <c:when test="${not empty empleado}">
                    <fmt:message key="empleado.editar"/> | SaludBoyaca
                </c:when>
                <c:otherwise>
                    <fmt:message key="empleado.nuevo"/> | SaludBoyaca
                </c:otherwise>
            </c:choose>
        </title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />
        <div class="container mt-4" style="max-width: 700px;">
            <div class="card">
                <div class="card-header-custom">
                    <i class="bi bi-person-badge"></i> 
                    <c:choose>
                        <c:when test="${not empty empleado}">
                            <fmt:message key="empleado.editar"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="empleado.nuevo"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/UsuarioServlet" method="post">
                        <input type="hidden" name="accion" value="${not empty empleado ? 'actualizar' : 'insertar'}">
                        <c:if test="${not empty empleado}">
                            <input type="hidden" name="id" value="${empleado.id}">
                        </c:if>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label><fmt:message key="empleado.nombres"/> *</label>
                                <input type="text" name="nombres" class="form-control" value="${empleado.nombres}" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label><fmt:message key="empleado.apellidos"/> *</label>
                                <input type="text" name="apellidos" class="form-control" value="${empleado.apellidos}" required>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label><fmt:message key="empleado.documento"/></label>
                                <input type="text" name="documento" class="form-control" value="${empleado.documento}">
                            </div>
                            <div class="col-md-6 mb-3">
                                <label><fmt:message key="empleado.email"/></label>
                                <input type="email" name="email" class="form-control" value="${empleado.email}">
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label><fmt:message key="empleado.username"/> *</label>
                                <input type="text" name="username" class="form-control" value="${empleado.username}" required>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label><fmt:message key="empleado.password"/> ${not empty empleado ? '(' : ''}<fmt:message key="empleado.password.mantener"/>${not empty empleado ? ')' : ''}</label>
                                <input type="password" name="password" class="form-control" ${empty empleado ? 'required' : ''}>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label><fmt:message key="empleado.rol"/> *</label>
                                <select name="rol" id="rolSelect" class="form-select" required>
                                    <option value="MEDICO" ${empleado.rol == 'MEDICO' ? 'selected' : ''}><fmt:message key="empleado.rol.medico"/></option>
                                    <option value="ENFERMERO" ${empleado.rol == 'ENFERMERO' ? 'selected' : ''}><fmt:message key="empleado.rol.enfermero"/></option>
                                    <option value="RECEPCIONISTA" ${empleado.rol == 'RECEPCIONISTA' ? 'selected' : ''}><fmt:message key="empleado.rol.recepcionista"/></option>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3 especialidad-field" id="especialidadDiv" style="display: ${empleado.rol == 'MEDICO' ? 'block' : 'none'};">
                                <label><fmt:message key="empleado.especialidad"/> (solo Médicos)</label>
                                <select name="especialidad" id="especialidad" class="form-select">
                                    <option value=""><fmt:message key="empleado.especialidad.seleccionar"/></option>
                                    <c:forEach var="esp" items="${especialidades}">
                                        <option value="${esp.nombre}" ${empleado.especialidad == esp.nombre ? 'selected' : ''}>${esp.nombre}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>
                        <div class="mb-3">
                            <label><fmt:message key="empleado.lang_preferido"/></label>
                            <select name="lang_preferido" class="form-select">
                                <option value="es" ${empleado.lang_preferido == 'es' ? 'selected' : ''}><fmt:message key="app.lang.es"/></option>
                                <option value="en" ${empleado.lang_preferido == 'en' ? 'selected' : ''}><fmt:message key="app.lang.en"/></option>
                            </select>
                        </div>
                        <div class="mb-3 form-check">
                            <input type="checkbox" name="activo" class="form-check-input" id="activo" ${empleado.activo ? 'checked' : ''}>
                            <label class="form-check-label" for="activo"><fmt:message key="empleado.activo"/></label>
                        </div>
                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-success"><i class="bi bi-save"></i> <fmt:message key="empleado.guardar"/></button>
                            <a href="${pageContext.request.contextPath}/UsuarioServlet?accion=listar" class="btn btn-secondary"><fmt:message key="empleado.cancelar"/></a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <script>
            document.getElementById('rolSelect').addEventListener('change', function () {
                var especialidadDiv = document.getElementById('especialidadDiv');
                if (this.value === 'MEDICO') {
                    especialidadDiv.style.display = 'block';
                } else {
                    especialidadDiv.style.display = 'none';
                    document.getElementById('especialidad').value = '';
                }
            });
            // Disparar al cargar para estado inicial
            if (document.getElementById('rolSelect').value === 'MEDICO') {
                document.getElementById('especialidadDiv').style.display = 'block';
            } else {
                document.getElementById('especialidadDiv').style.display = 'none';
            }
        </script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>