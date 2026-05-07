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
                <c:when test="${not empty horario}">
                    <fmt:message key="horario.editar"/> | SaludBoyaca
                </c:when>
                <c:otherwise>
                    <fmt:message key="horario.nuevo"/> | SaludBoyaca
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
                    <i class="bi bi-clock"></i> 
                    <c:choose>
                        <c:when test="${not empty horario}">
                            <fmt:message key="horario.editar"/>
                        </c:when>
                        <c:otherwise>
                            <fmt:message key="horario.nuevo"/>
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="card-body">
                    <form id="horarioForm" action="${pageContext.request.contextPath}/HorarioServlet" method="post">
                        <input type="hidden" name="accion" value="${not empty horario ? 'actualizar' : 'insertar'}">
                        <c:if test="${not empty horario}">
                            <input type="hidden" name="id" value="${horario.id}">
                        </c:if>

                        <!-- Campo oculto para el ID del personal seleccionado -->
                        <input type="hidden" name="idMedico" id="idMedicoHidden" value="${horario.idMedico}">

                        <div class="mb-3">
                            <label class="form-label"><fmt:message key="horario.personal"/> *</label>
                            <input type="text" id="personalInput" class="form-control" 
                                   placeholder="<fmt:message key="horario.personal.placeholder"/>" 
                                   autocomplete="off" list="personalList">
                            <datalist id="personalList">
                                <c:forEach var="persona" items="${personal}">
                                    <option value="${persona.nombres} ${persona.apellidos} (${persona.rol})" data-id="${persona.id}"></option>
                                </c:forEach>
                            </datalist>
                            <div class="form-text"><fmt:message key="horario.personal.help"/></div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label"><fmt:message key="horario.dia"/> *</label>
                            <select name="diaSemana" class="form-select" required>
                                <option value=""><fmt:message key="horario.dia.seleccionar"/></option>
                                <option value="1" ${horario.diaSemana == 1 ? 'selected' : ''}><fmt:message key="horario.dia.lunes"/></option>
                                <option value="2" ${horario.diaSemana == 2 ? 'selected' : ''}><fmt:message key="horario.dia.martes"/></option>
                                <option value="3" ${horario.diaSemana == 3 ? 'selected' : ''}><fmt:message key="horario.dia.miercoles"/></option>
                                <option value="4" ${horario.diaSemana == 4 ? 'selected' : ''}><fmt:message key="horario.dia.jueves"/></option>
                                <option value="5" ${horario.diaSemana == 5 ? 'selected' : ''}><fmt:message key="horario.dia.viernes"/></option>
                                <option value="6" ${horario.diaSemana == 6 ? 'selected' : ''}><fmt:message key="horario.dia.sabado"/></option>
                                <option value="7" ${horario.diaSemana == 7 ? 'selected' : ''}><fmt:message key="horario.dia.domingo"/></option>
                            </select>
                        </div>

                        <div class="row">
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label class="form-label"><fmt:message key="horario.hora_inicio"/> *</label>
                                    <input type="time" name="horaInicio" class="form-control" value="${horario.horaInicio}" required>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="mb-3">
                                    <label class="form-label"><fmt:message key="horario.hora_fin"/> *</label>
                                    <input type="time" name="horaFin" class="form-control" value="${horario.horaFin}" required>
                                </div>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label"><fmt:message key="horario.max_citas"/> *</label>
                            <input type="number" name="maxCitas" class="form-control" value="${horario.maxCitas}" required min="1">
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-success"><i class="bi bi-save"></i> <fmt:message key="horario.guardar"/></button>
                            <a href="${pageContext.request.contextPath}/HorarioServlet?accion=listar" class="btn btn-secondary"><fmt:message key="horario.cancelar"/></a>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            (function () {
                const inputPersonal = document.getElementById('personalInput');
                const hiddenId = document.getElementById('idMedicoHidden');
                const datalist = document.getElementById('personalList');

                // Mapear texto visible -> ID
                const textoToId = new Map();
                Array.from(datalist.options).forEach(opt => {
                    textoToId.set(opt.value, opt.getAttribute('data-id'));
                });

                function syncHidden() {
                    const texto = inputPersonal.value;
                    if (textoToId.has(texto)) {
                        hiddenId.value = textoToId.get(texto);
                        inputPersonal.classList.remove('is-invalid');
                    } else {
                        hiddenId.value = '';
                        if (texto.trim() !== '') {
                            inputPersonal.classList.add('is-invalid');
                        } else {
                            inputPersonal.classList.remove('is-invalid');
                        }
                    }
                }

                inputPersonal.addEventListener('input', syncHidden);
                inputPersonal.addEventListener('change', syncHidden);

                // Validación antes de enviar (texto fijo, sin i18n)
                const form = document.getElementById('horarioForm');
                form.addEventListener('submit', function (e) {
                    if (!hiddenId.value || hiddenId.value === '') {
                        e.preventDefault();
                        alert('Por favor, seleccione un personal válido de la lista.');
                        inputPersonal.focus();
                        return false;
                    }
                    return true;
                });

                // Cargar el nombre correspondiente si estamos editando
                const idInicial = hiddenId.value;
                if (idInicial) {
                    let textoEncontrado = null;
                    for (let [texto, id] of textoToId.entries()) {
                        if (id == idInicial) {
                            textoEncontrado = texto;
                            break;
                        }
                    }
                    if (textoEncontrado) {
                        inputPersonal.value = textoEncontrado;
                        inputPersonal.classList.remove('is-invalid');
                    } else {
                        inputPersonal.value = 'ID: ' + idInicial;
                    }
                }
            })();
        </script>
    </body>
</html>