<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<c:if test="${empty totalPacientes}">
    <c:redirect url="/dashboard" />
</c:if>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title><fmt:message key="nav.dashboard"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Inter:opsz,wght@14..32,300;400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/estiloDash.css">
    </head>
    <body>
        <jsp:include page="templates/header.jsp" />

        <div class="container mt-4">
            <!-- Bienvenida -->
            <div class="row mb-4">
                <div class="col-12">
                    <h1 class="titulo-modulo"><fmt:message key="dashboard.titulo"/></h1>
                    <p class="text-muted"><fmt:message key="dashboard.subtitulo"/></p>
                </div>
            </div>

            <!-- Tarjetas de estadísticas principales -->
            <div class="row g-4 mb-5">
                <div class="col-md-3">
                    <div class="stats-card">
                        <div class="stats-icon" style="background-color: var(--azul-salud);">
                            <i class="bi bi-people-fill"></i>
                        </div>
                        <div class="stats-number">${totalPacientes}</div>
                        <div class="stats-label"><fmt:message key="dashboard.pacientes.total"/></div>
                        <div class="stats-trend">
                            <i class="bi bi-graph-up trend-up"></i>
                            <span class="trend-up"><fmt:message key="dashboard.trend.up"/></span>
                        </div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card">
                        <div class="stats-icon" style="background-color: var(--verde-sena);">
                            <i class="bi bi-calendar-check"></i>
                        </div>
                        <div class="stats-number">${totalCitas}</div>
                        <div class="stats-label"><fmt:message key="dashboard.total.citas"/></div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card">
                        <div class="stats-icon" style="background-color: var(--celeste-suave);">
                            <i class="bi bi-calendar-day"></i>
                        </div>
                        <div class="stats-number">${citasHoy}</div>
                        <div class="stats-label"><fmt:message key="dashboard.citas.hoy"/></div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="stats-card">
                        <div class="stats-icon" style="background-color: var(--morado);">
                            <i class="bi bi-clock-history"></i>
                        </div>
                        <div class="stats-number">${totalHorarios}</div>
                        <div class="stats-label"><fmt:message key="dashboard.horarios.activos"/></div>
                    </div>
                </div>
            </div>

            <!-- Gráfico de citas por estado + Empleados -->
            <div class="row g-4 mb-5">
                <div class="col-lg-6">
                    <div class="chart-container">
                        <h4 class="section-title-dash"><i class="bi bi-pie-chart"></i> <fmt:message key="dashboard.citas.estado.titulo"/></h4>
                        <canvas id="citasEstadoChart" width="400" height="250"></canvas>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="chart-container">
                        <h4 class="section-title-dash"><i class="bi bi-person-badge"></i> <fmt:message key="dashboard.personal.rol.titulo"/></h4>
                        <canvas id="personalChart" width="400" height="250"></canvas>
                    </div>
                </div>
            </div>

            <!-- Últimas citas registradas -->
            <div class="row mt-4">
                <div class="col-12">
                    <div class="card">
                        <div class="card-header-custom d-flex justify-content-between align-items-center">
                            <span><i class="bi bi-recent"></i> <fmt:message key="dashboard.ultimas.citas"/></span>
                            <a href="${pageContext.request.contextPath}/CitaServlet?accion=listar" class="btn btn-sm btn-outline-primary">
                                <fmt:message key="accion.ver.todas"/>
                            </a>
                        </div>
                        <div class="table-responsive">
                            <table class="table table-hover table-dashboard mb-0">
                                <thead>
                                    <tr>
                                        <th><fmt:message key="cita.paciente"/></th>
                                        <th><fmt:message key="cita.medico"/></th>
                                        <th><fmt:message key="cita.fecha"/></th>
                                        <th><fmt:message key="cita.estado"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="c" items="${ultimasCitas}" begin="0" end="4">
                                        <tr>
                                            <td>${c.nombrePaciente}</td>
                                            <td>${c.nombreMedico}</td>
                                            <td>${c.fechaCita}</td>
                                            <td>
                                                <span class="badge 
                                                      ${c.estado == 'PROGRAMADA' ? 'badge-programada' : 
                                                        (c.estado == 'CONFIRMADA' ? 'badge-confirmada' : 
                                                        (c.estado == 'ATENDIDA' ? 'badge-atendida' : 'badge-cancelada'))}">
                                                      <fmt:message key="cita.estado.${c.estado.toLowerCase()}"/>
                                                </span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    <c:if test="${empty ultimasCitas}">
                                    <td><td colspan="4" class="text-center text-muted"><fmt:message key="dashboard.ultimas.vacia"/></td></tr>
                                </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Etiquetas internacionalizadas para los gráficos
            const labelsCitas = [
                '<fmt:message key="cita.estado.programada"/>',
                '<fmt:message key="cita.estado.confirmada"/>',
                '<fmt:message key="cita.estado.atendida"/>',
                '<fmt:message key="cita.estado.cancelada"/>'
            ];
            const labelsPersonal = [
                '<fmt:message key="empleado.rol.medico"/>',
                '<fmt:message key="empleado.rol.enfermero"/>',
                '<fmt:message key="empleado.rol.recepcionista"/>'
            ];

            // Gráfico de citas por estado
            const ctx1 = document.getElementById('citasEstadoChart').getContext('2d');
            new Chart(ctx1, {
                type: 'doughnut',
                data: {
                    labels: labelsCitas,
                    datasets: [{
                            data: [${citasProgramadas}, ${citasConfirmadas}, ${citasAtendidas}, ${citasCanceladas}],
                            backgroundColor: ['#F39C12', '#27AE60', '#2980B9', '#E74C3C'],
                            borderWidth: 0
                        }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: true,
                    plugins: {
                        legend: {position: 'bottom'}
                    }
                }
            });

            // Gráfico de personal por rol
            const ctx2 = document.getElementById('personalChart').getContext('2d');
            new Chart(ctx2, {
                type: 'bar',
                data: {
                    labels: labelsPersonal,
                    datasets: [{
                            label: '<fmt:message key="dashboard.personal.rol.cantidad"/>',
                            data: [${totalMedicos}, ${totalEnfermeros}, ${totalRecepcionistas}],
                            backgroundColor: '#1A5276',
                            borderRadius: 8
                        }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            stepSize: 1,
                            grid: {color: '#e9ecef'},
                            title: {
                                display: true,
                                text: '<fmt:message key="dashboard.personal.rol.cantidad"/>'
                            }
                        }
                    }
                }
            });
        </script>
    </body>
</html>