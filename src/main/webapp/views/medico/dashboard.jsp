<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<!DOCTYPE html>
<html>
    <head>
        <title><fmt:message key="nav.medico.panel"/> | SaludBoyaca</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Inter:opsz,wght@14..32,300;400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
        <style>
            body {
                background: linear-gradient(135deg, #f5f7fc 0%, #e9eef5 100%);
                font-family: 'Inter', sans-serif;
            }
            .dashboard-header {
                text-align: center;
                margin-bottom: 2rem;
            }
            .dashboard-header h2 {
                font-weight: 700;
                background: linear-gradient(105deg, #1A5276, #2E86C1);
                -webkit-background-clip: text;
                background-clip: text;
                color: transparent;
                display: inline-block;
            }
            .dashboard-header p {
                color: #5a6e7c;
                font-size: 1rem;
            }
            .card-dashboard {
                background: rgba(255, 255, 255, 0.95);
                backdrop-filter: blur(2px);
                border: none;
                border-radius: 2rem;
                transition: all 0.3s ease;
                box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.02);
                overflow: hidden;
                height: 100%;
            }
            .card-dashboard:hover {
                transform: translateY(-8px);
                box-shadow: 0 20px 35px -12px rgba(0, 0, 0, 0.2);
            }
            .card-icon {
                background: linear-gradient(145deg, #1A5276, #0f3b5e);
                width: 80px;
                height: 80px;
                border-radius: 30px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                margin-bottom: 1.5rem;
                box-shadow: 0 15px 20px -10px rgba(26, 82, 118, 0.3);
            }
            .card-icon i {
                font-size: 2.6rem;
                color: white;
            }
            .card-title {
                font-size: 1.6rem;
                font-weight: 700;
                color: #1A5276;
                margin-bottom: 0.5rem;
            }
            .card-text {
                color: #5a6e7c;
                font-size: 0.9rem;
                margin-bottom: 1.5rem;
            }
            .btn-dashboard {
                background: linear-gradient(105deg, #39A900, #2e8a00);
                border: none;
                padding: 0.6rem 1.2rem;
                font-weight: 600;
                border-radius: 3rem;
                transition: all 0.2s;
                box-shadow: 0 4px 8px rgba(57, 169, 0, 0.2);
            }
            .btn-dashboard-secondary {
                background: linear-gradient(105deg, #1A5276, #0f3b5e);
                border: none;
                padding: 0.6rem 1.2rem;
                font-weight: 600;
                border-radius: 3rem;
                transition: all 0.2s;
                box-shadow: 0 4px 8px rgba(26, 82, 118, 0.2);
            }
            .btn-dashboard:hover, .btn-dashboard-secondary:hover {
                transform: scale(1.02);
                filter: brightness(1.05);
            }
            .footer-note {
                text-align: center;
                margin-top: 3rem;
                color: #6c7e8f;
                font-size: 0.85rem;
            }
            @media (max-width: 768px) {
                .card-icon {
                    width: 60px;
                    height: 60px;
                }
                .card-icon i {
                    font-size: 2rem;
                }
                .card-title {
                    font-size: 1.3rem;
                }
            }
        </style>
    </head>
    <body>
        <jsp:include page="../templates/header.jsp" />

        <div class="container mt-5">
            <div class="dashboard-header">
                <h2>
                    <fmt:message key="dashboard.medico.bienvenida">
                        <fmt:param value="${sessionScope.usuario.nombres}" />
                        <fmt:param value="${sessionScope.usuario.apellidos}" />
                    </fmt:message>
                </h2>
                <p class="mt-2">
                    <i class="bi bi-clock-history"></i> 
                    <fmt:message key="dashboard.medico.subtitulo"/>
                </p>
            </div>

            <div class="row justify-content-center g-4">
                <div class="col-md-5">
                    <div class="card-dashboard text-center p-4">
                        <div class="card-icon mx-auto">
                            <i class="bi bi-calendar-check"></i>
                        </div>
                        <h3 class="card-title"><fmt:message key="nav.mis_citas"/></h3>
                        <p class="card-text">
                            <fmt:message key="dashboard.medico.citas.desc"/>
                        </p>
                        <a href="${pageContext.request.contextPath}/CitaServlet?accion=listar" class="btn btn-dashboard text-white">
                            <i class="bi bi-eye me-2"></i> <fmt:message key="accion.ver.citas"/>
                        </a>
                    </div>
                </div>
                <div class="col-md-5">
                    <div class="card-dashboard text-center p-4">
                        <div class="card-icon mx-auto">
                            <i class="bi bi-file-pdf"></i>
                        </div>
                        <h3 class="card-title"><fmt:message key="cita.descargar.pdf"/></h3>
                        <p class="card-text">
                            <fmt:message key="dashboard.medico.pdf.desc"/>
                        </p>
                        <a href="${pageContext.request.contextPath}/CitaServlet?accion=exportar" class="btn btn-dashboard-secondary text-white">
                            <i class="bi bi-download me-2"></i> <fmt:message key="accion.descargar"/>
                        </a>
                    </div>
                </div>
            </div>

            <div class="footer-note">
                <i class="bi bi-shield-check"></i> <fmt:message key="dashboard.medico.nota"/>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>