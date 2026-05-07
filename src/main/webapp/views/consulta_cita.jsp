<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><fmt:message key="consulta.titulo"/> - SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Inter:opsz,wght@14..32,300;400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
        <style>
            body {
                background: linear-gradient(135deg, #e5f0fb 0%, #cde3f5 100%);
                font-family: 'Inter', sans-serif;
            }
            .consulta-card {
                border-radius: 1.5rem !important;
                overflow: hidden;
                box-shadow: 0 20px 35px -10px rgba(0,0,0,0.2);
                transition: transform 0.2s;
            }
            .consulta-card:hover {
                transform: translateY(-5px);
            }
            .card-header-custom {
                background: linear-gradient(105deg, #1A5276, #2E86C1);
                border-bottom: none;
            }
            .btn-search {
                background: linear-gradient(105deg, #39A900, #5EDC1A);
                border: none;
                padding: 10px;
                font-weight: 600;
                transition: all 0.2s;
            }
            .btn-search:hover {
                transform: scale(1.02);
                background: linear-gradient(105deg, #2e8a00, #4bbd12);
            }
            .captcha-img {
                border-radius: 12px;
                border: 1px solid #d4e6f1;
                transition: 0.2s;
            }
            .captcha-img:hover {
                opacity: 0.9;
                box-shadow: 0 0 8px rgba(46,134,193,0.5);
            }
            .table-custom {
                border-radius: 1rem;
                overflow: hidden;
            }
            .badge-programada {
                background-color: #F39C12;
            }
            .badge-confirmada {
                background-color: #27AE60;
            }
            .badge-atendida   {
                background-color: #2980B9;
            }
            .badge-cancelada  {
                background-color: #E74C3C;
            }
            .btn-pdf {
                background-color: #dc3545;
                color: white;
                border: none;
                border-radius: 20px;
                padding: 4px 12px;
                font-size: 0.8rem;
                transition: 0.2s;
            }
            .btn-pdf:hover {
                background-color: #b02a37;
                transform: translateY(-2px);
            }
        </style>
    </head>
    <body>
        <div class="container mt-5">
            <div class="row justify-content-center">
                <div class="col-md-10">
                    <div class="card consulta-card shadow-lg border-0">
                        <div class="card-header-custom text-white text-center p-4">
                            <i class="bi bi-calendar-check fs-1"></i>
                            <h2 class="h4 mt-2 fw-semibold"><fmt:message key="consulta.titulo"/></h2>
                        </div>
                        <div class="card-body p-4 p-md-5 bg-white">
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger alert-dismissible fade show">
                                    <i class="bi bi-exclamation-triangle-fill me-2"></i> ${error}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>
                            <form action="${pageContext.request.contextPath}/consulta-cita" method="post" class="row g-4">
                                <div class="col-12">
                                    <label for="documento" class="form-label fw-semibold"><fmt:message key="consulta.documento"/></label>
                                    <input type="text" class="form-control form-control-lg" id="documento" name="documento" 
                                           value="${documento}" placeholder="<fmt:message key="consulta.documento.placeholder"/>" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="captcha" class="form-label fw-semibold"><fmt:message key="consulta.captcha"/></label>
                                    <input type="text" class="form-control" id="captcha" name="captcha" 
                                           placeholder="<fmt:message key="consulta.captcha.placeholder"/>" required>
                                </div>
                                <div class="col-md-6 text-center">
                                    <img src="${pageContext.request.contextPath}/captcha" alt="CAPTCHA" 
                                         class="captcha-img img-fluid" onclick="this.src = '${pageContext.request.contextPath}/captcha?' + Math.random()">
                                    <div class="form-text mt-2"><fmt:message key="consulta.captcha.recargar"/></div>
                                </div>
                                <div class="col-12">
                                    <button type="submit" class="btn btn-search w-100 btn-lg text-white">
                                        <i class="bi bi-search me-2"></i> <fmt:message key="consulta.buscar"/>
                                    </button>
                                </div>
                            </form>
                            <div class="mt-4 text-center">
                                <a href="${pageContext.request.contextPath}/index.jsp" class="text-decoration-none text-secondary">
                                    <i class="bi bi-arrow-left-circle"></i> <fmt:message key="accion.volver.inicio"/>
                                </a>
                            </div>
                        </div>
                    </div>

                    <!-- Resultados de la consulta -->
                    <c:if test="${not empty citas}">
                        <div class="card mt-5 shadow-sm rounded-4 border-0">
                            <div class="card-header bg-success text-white rounded-top-4">
                                <h5 class="mb-0"><i class="bi bi-list-ul me-2"></i> <fmt:message key="consulta.resultados"/></h5>
                            </div>
                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table table-hover mb-0 table-custom">
                                        <thead class="table-light">
                                            <tr>
                                                <th><fmt:message key="cita.fecha"/></th>
                                                <th><fmt:message key="cita.hora"/></th>
                                                <th><fmt:message key="cita.medico"/></th>
                                                <th><fmt:message key="cita.especialidad"/></th>
                                                <th><fmt:message key="cita.motivo"/></th>
                                                <th><fmt:message key="cita.estado"/></th>
                                                <th><fmt:message key="accion.acciones"/></th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="c" items="${citas}">
                                                <tr>
                                                    <td>${c.fechaCita}</td>
                                                    <td>${c.horaCita}</td>
                                                    <td>${c.nombreMedico}</td>
                                                    <td>${c.nombreEspecialidad}<td>
                                                    <td>${c.motivo}</td>
                                                    <td>
                                                        <span class="badge 
                                                              ${c.estado == 'PROGRAMADA' ? 'badge-programada' : 
                                                                (c.estado == 'CONFIRMADA' ? 'badge-confirmada' : 
                                                                (c.estado == 'ATENDIDA' ? 'badge-atendida' : 'badge-cancelada'))}">
                                                              <fmt:message key="cita.estado.${c.estado.toLowerCase()}"/>
                                                        </span>
                                                    </td>
                                                    <td>
                                                        <a href="${pageContext.request.contextPath}/exportar-cita-individual?id=${c.id}" 
                                                           class="btn btn-pdf" title="<fmt:message key="cita.descargar.pdf"/>">
                                                            <i class="bi bi-file-pdf"></i> PDF
                                                        </a>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <c:if test="${not empty param.documento and empty citas}">
                        <div class="alert alert-info mt-4 shadow-sm"><i class="bi bi-info-circle me-2"></i> <fmt:message key="consulta.no.encontrado"/></div>
                    </c:if>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>