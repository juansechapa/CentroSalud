<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />

<!DOCTYPE html>
<html>
    <head>
        <title><fmt:message key="otp.titulo"/></title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Inter:opsz,wght@14..32,300;400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/saluboyaca.css">
        <style>
            /* Estilos adicionales para la vista OTP */
            body {
                background: linear-gradient(135deg, #e0f0ff 0%, #c2e0ff 100%);
                min-height: 100vh;
                display: flex;
                align-items: center;
                justify-content: center;
                font-family: 'Inter', sans-serif;
            }
            .otp-card {
                backdrop-filter: blur(2px);
                background: rgba(255, 255, 255, 0.95);
                border-radius: 2rem !important;
                box-shadow: 0 20px 35px -10px rgba(0, 0, 0, 0.2), 0 0 0 1px rgba(255,255,255,0.5) inset;
                transition: transform 0.2s ease;
            }
            .otp-card:hover {
                transform: translateY(-5px);
            }
            .otp-header {
                background: linear-gradient(105deg, #1A5276 0%, #2E86C1 100%);
                border-radius: 2rem 2rem 1rem 1rem;
                padding: 1.2rem;
                border: none;
            }
            .otp-header i {
                filter: drop-shadow(0 4px 6px rgba(0,0,0,0.1));
            }
            .form-control-otp {
                font-size: 1.6rem;
                letter-spacing: 0.5rem;
                font-weight: 600;
                text-align: center;
                background-color: #f8fcff;
                border: 2px solid #d4e6f1;
                border-radius: 1rem;
                transition: all 0.2s;
            }
            .form-control-otp:focus {
                border-color: #2E86C1;
                box-shadow: 0 0 0 0.25rem rgba(46,134,193,0.25);
                background-color: white;
            }
            .btn-otp {
                background: linear-gradient(105deg, #39A900 0%, #5EDC1A 100%);
                border: none;
                padding: 0.8rem;
                font-weight: 700;
                border-radius: 2rem;
                transition: all 0.2s;
                box-shadow: 0 4px 8px rgba(0,0,0,0.05);
            }
            .btn-otp:hover {
                transform: translateY(-2px);
                box-shadow: 0 8px 20px rgba(57,169,0,0.3);
                background: linear-gradient(105deg, #2e8a00 0%, #4bbd12 100%);
            }
            .recovery-link {
                color: #1A5276;
                text-decoration: none;
                font-weight: 500;
                border-bottom: 1px dashed #1A5276;
                transition: 0.2s;
            }
            .recovery-link:hover {
                color: #2E86C1;
                border-bottom-color: #2E86C1;
            }
            .footer-note {
                font-size: 0.8rem;
                margin-top: 1.5rem;
                text-align: center;
                color: #5b7c9c;
            }
            .footer-note a {
                color: #2E86C1;
                text-decoration: none;
            }
            .footer-note a:hover {
                text-decoration: underline;
            }
            @media (max-width: 576px) {
                .form-control-otp {
                    font-size: 1.2rem;
                    letter-spacing: 0.2rem;
                }
            }
        </style>
    </head>
    <body>

        <div class="container mt-5">
            <div class="row justify-content-center">
                <div class="col-md-5">
                    <div class="card otp-card shadow border-0">
                        <div class="card-header otp-header text-white text-center">
                            <i class="bi bi-shield-lock-fill fs-1"></i>
                            <h4 class="mt-2 fw-semibold"><fmt:message key="otp.titulo"/></h4>
                        </div>
                        <div class="card-body p-4 p-md-5">
                            <p class="text-center text-muted mb-4">
                                <fmt:message key="otp.instruccion"/>
                            </p>
                            <c:if test="${not empty error}">
                                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                    <i class="bi bi-exclamation-triangle-fill me-2"></i> ${error}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>
                            <form id="otpForm" action="${pageContext.request.contextPath}/verificar-otp" method="post">
                                <div class="mb-4">
                                    <label class="form-label fw-semibold"><fmt:message key="otp.campo"/></label>
                                    <input type="text" name="codigo" class="form-control form-control-otp" 
                                           maxlength="6" placeholder="<fmt:message key="otp.campo.placeholder"/>" required autofocus>
                                </div>
                                <button type="submit" class="btn btn-otp w-100 btn-lg text-white">
                                    <i class="bi bi-check2-circle me-2"></i> <fmt:message key="otp.verificar"/>
                                </button>
                            </form>
                            <div class="text-center mt-4">
                                <a href="${pageContext.request.contextPath}/reenviar-otp" id="reenviarLink" class="recovery-link">
                                    <i class="bi bi-envelope-paper me-1"></i> <fmt:message key="otp.reenviar"/>
                                </a>
                            </div>
                            <div class="footer-note">
                                <i class="bi bi-arrow-return-left"></i> <a href="${pageContext.request.contextPath}/views/login.jsp"><fmt:message key="accion.volver"/></a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script>
    var mensajeReenvioOk = '<fmt:message key="otp.reenviar.ok"/>';
    var mensajeReenvioError = '<fmt:message key="otp.reenviar.error"/>';

    document.getElementById('reenviarLink').addEventListener('click', function (e) {
        e.preventDefault();
        fetch('${pageContext.request.contextPath}/reenviar-otp', {method: 'POST'})
                .then(response => {
                    if (response.ok) {
                        alert(mensajeReenvioOk);
                    } else {
                        alert(mensajeReenvioError);
                    }
                })
                .catch(() => alert(mensajeReenvioError));
    });
        </script>
    </body>
</html>