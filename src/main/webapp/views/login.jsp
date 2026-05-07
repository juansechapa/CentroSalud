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
        <title><fmt:message key="app.nombre"/></title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Inter:opsz,wght@14..32,300;400;500;600;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Login.css">
    </head>
    <body>

        <!-- ======================== NAVBAR ======================== -->
        <nav class="navbar navbar-expand-lg navbar-salud sticky-top">
            <div class="container">
                <a class="navbar-brand fw-bold" href="#">
                    <i class="bi bi-heart-pulse me-2"></i> <fmt:message key="app.nombre"/>
                </a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarMain">
                    <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                        <li class="nav-item"><a class="nav-link active" href="#"><fmt:message key="nav.inicio"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="#servicios"><fmt:message key="nav.servicios"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="#horarios"><fmt:message key="nav.horarios"/></a></li>
                    </ul>
                    <div class="d-flex gap-2 ms-lg-3">
                        <!-- Selector de idioma -->
                        <div class="dropdown">
                            <button class="btn btn-outline-light btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown">
                                <i class="bi bi-globe2"></i> <fmt:message key="app.lang.seleccionar"/>
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="?lang=es"><fmt:message key="app.lang.es"/></a></li>
                                <li><a class="dropdown-item" href="?lang=en"><fmt:message key="app.lang.en"/></a></li>
                            </ul>
                        </div>
                        <!-- Botón que abre la modal de login para personal -->
                        <button type="button" class="btn btn-outline-light btn-sm" data-bs-toggle="modal" data-bs-target="#loginModal">
                            <i class="bi bi-box-arrow-in-right"></i> <fmt:message key="nav.acceso"/>
                        </button>
                    </div>
                </div>
            </div>
        </nav>

        <!-- ======================== HERO SECTION ======================== -->
        <section class="hero">
            <div class="container text-center">
                <h1 class="display-5 fw-bold"><fmt:message key="hero.titulo"/></h1>
                <p class="lead mt-3"><fmt:message key="hero.descripcion"/></p>
                <a href="#servicios" class="btn btn-success btn-lg px-4">
                    <i class="bi bi-calendar-heart me-2"></i> <fmt:message key="hero.boton.servicios"/>
                </a>
                <!-- Botón que redirige a la consulta pública de citas (sin login) -->
                <a href="${pageContext.request.contextPath}/consulta-cita" class="btn btn-outline-light btn-lg ms-2">
                    <i class="bi bi-search"></i> <fmt:message key="hero.boton.consultar"/>
                </a>
            </div>
        </section>

        <!-- ======================== SERVICIOS DESTACADOS ======================== -->
        <div class="container" id="servicios">
            <div class="text-center">
                <h2 class="section-title"><fmt:message key="servicios.titulo"/></h2>
                <p class="text-muted mb-5"><fmt:message key="servicios.subtitulo"/></p>
            </div>
            <div class="row g-4">
                <div class="col-md-4">
                    <div class="card h-100 text-center p-4">
                        <div class="icon-circle"><i class="bi bi-stethoscope"></i></div>
                        <h5 class="card-title"><fmt:message key="servicio.consulta.titulo"/></h5>
                        <p class="card-text"><fmt:message key="servicio.consulta.desc"/></p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 text-center p-4">
                        <div class="icon-circle"><i class="bi bi-droplet"></i></div>
                        <h5 class="card-title"><fmt:message key="servicio.vacunacion.titulo"/></h5>
                        <p class="card-text"><fmt:message key="servicio.vacunacion.desc"/></p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 text-center p-4">
                        <div class="icon-circle"><i class="bi bi-heart-pulse"></i></div>
                        <h5 class="card-title"><fmt:message key="servicio.especialidades.titulo"/></h5>
                        <p class="card-text"><fmt:message key="servicio.especialidades.desc"/></p>
                    </div>
                </div>
            </div>
            <div class="row g-4 mt-2">
                <div class="col-md-4">
                    <div class="card h-100 text-center p-4">
                        <div class="icon-circle"><i class="bi bi-capsule"></i></div>
                        <h5 class="card-title"><fmt:message key="servicio.farmacia.titulo"/></h5>
                        <p class="card-text"><fmt:message key="servicio.farmacia.desc"/></p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 text-center p-4">
                        <div class="icon-circle"><i class="bi bi-activity"></i></div>
                        <h5 class="card-title"><fmt:message key="servicio.laboratorio.titulo"/></h5>
                        <p class="card-text"><fmt:message key="servicio.laboratorio.desc"/></p>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="card h-100 text-center p-4">
                        <div class="icon-circle"><i class="bi bi-calendar-week"></i></div>
                        <h5 class="card-title"><fmt:message key="servicio.agendamiento.titulo"/></h5>
                        <p class="card-text"><fmt:message key="servicio.agendamiento.desc"/></p>
                    </div>
                </div>
            </div>
        </div>

        <!-- ======================== HORARIOS Y UBICACIÓN ======================== -->
        <div class="container mt-5" id="horarios">
            <div class="row align-items-center">
                <div class="col-lg-6">
                    <h2 class="section-title"><fmt:message key="horarios.titulo"/></h2>
                    <div class="horario-item">
                        <i class="bi bi-clock-fill me-2" style="color: var(--azul-salud);"></i>
                        <strong><fmt:message key="horarios.lunes.viernes"/>:</strong> 7:00 am – 7:00 pm
                    </div>
                    <div class="horario-item">
                        <i class="bi bi-clock-fill me-2" style="color: var(--azul-salud);"></i>
                        <strong><fmt:message key="horarios.sabado"/>:</strong> 8:00 am – 2:00 pm
                    </div>
                    <div class="horario-item">
                        <i class="bi bi-clock-fill me-2" style="color: var(--azul-salud);"></i>
                        <strong><fmt:message key="horarios.domingo"/>:</strong> <fmt:message key="horarios.domingo.texto"/>
                    </div>
                    <div class="mt-4">
                        <i class="bi bi-geo-alt-fill me-2" style="color: var(--verde-sena);"></i>
                        <strong><fmt:message key="contacto.direccion"/></strong> <fmt:message key="contacto.direccion.texto"/>
                        <br>
                        <i class="bi bi-telephone-fill me-2"></i> <fmt:message key="contacto.telefono"/> <fmt:message key="contacto.telefono.numero"/>
                        <br>
                        <i class="bi bi-envelope-fill me-2"></i> <fmt:message key="contacto.email"/> <fmt:message key="contacto.email.valor"/>
                    </div>
                    <div class="mt-4">
                        <a href="#" class="btn btn-primary"><i class="bi bi-calendar-check"></i> <fmt:message key="contacto.agendar"/></a>
                    </div>
                </div>
                <div class="col-lg-6 mt-4 mt-lg-0">
                    <div class="card p-3">
                        <iframe src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3960.804269324208!2d-72.943119!3d5.703687!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x8e6a5d9e1d6b1b5f%3A0x6b9c7d6a1b2c3d4e!2sNobsa%2C%20Boyac%C3%A1!5e0!3m2!1ses!2sco!4v1647890123456!5m2!1ses!2sco" width="100%" height="250" style="border:0; border-radius: 16px;" allowfullscreen="" loading="lazy"></iframe>
                        <div class="mt-2 text-center">
                            <small class="text-muted"><i class="bi bi-pin-map"></i> <fmt:message key="contacto.mapa.texto"/></small>
                        </div>
                    </div>
                </div>
            </div>
        </div>


        <!-- ======================== MODAL DE LOGIN ======================== -->
        <div class="modal fade" id="loginModal" tabindex="-1" aria-labelledby="loginModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="loginModalLabel">
                            <i class="bi bi-person-badge me-2"></i> <fmt:message key="login.titulo"/>
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="<fmt:message key="accion.cerrar"/>"></button>
                    </div>
                    <div class="modal-body">
                        <div id="loginAlert" class="alert alert-danger d-none" role="alert"></div>
                        <form id="loginForm" method="POST" action="${pageContext.request.contextPath}/login">
                            <div class="mb-3">
                                <label for="username" class="form-label"><i class="bi bi-person"></i> <fmt:message key="login.usuario"/></label>
                                <input type="text" class="form-control" id="username" name="username" placeholder="<fmt:message key="login.usuario.placeholder"/>" required>
                            </div>
                            <div class="mb-3">
                                <label for="password" class="form-label"><i class="bi bi-key"></i> <fmt:message key="login.contrasena"/></label>
                                <input type="password" class="form-control" id="password" name="password" placeholder="<fmt:message key="login.contrasena.placeholder"/>" required>
                            </div>
                            <div class="mb-3 form-check">
                                <input type="checkbox" class="form-check-input" id="remember" name="remember">
                                <label class="form-check-label" for="remember"><fmt:message key="login.recordar"/></label>
                            </div>

                            <button type="submit" class="btn btn-login-modal">
                                <i class="bi bi-box-arrow-in-right"></i> <fmt:message key="login.ingresar"/>
                            </button>
                        </form>
                    </div>
                    <div class="modal-footer justify-content-center">
                        <small class="text-muted"><fmt:message key="login.sin.cuenta"/> <a href="#"><fmt:message key="login.contactar.admin"/></a></small>
                    </div>
                </div>
            </div>
        </div>


        <!-- ======================== FOOTER ======================== -->
        <footer>
            <div class="container">
                <div class="row">
                    <div class="col-md-4">
                        <h5><i class="bi bi-heart-pulse"></i> <fmt:message key="app.nombre"/></h5>
                        <p class="small"><fmt:message key="footer.descripcion"/></p>
                    </div>
                    <div class="col-md-3">
                        <h6><fmt:message key="footer.enlaces"/></h6>
                        <ul class="list-unstyled">
                            <li><a href="#"><fmt:message key="footer.agendar"/></a></li>
                            <li><a href="#"><fmt:message key="footer.resultados"/></a></li>
                            <li><a href="#"><fmt:message key="footer.preguntas"/></a></li>
                        </ul>
                    </div>
                    <div class="col-md-5">
                        <h6><fmt:message key="footer.boletin"/></h6>
                        <div class="input-group">
                            <input type="email" class="form-control" placeholder="<fmt:message key="footer.email.placeholder"/>">
                            <button class="btn btn-success" type="button"><fmt:message key="footer.suscribir"/></button>
                        </div>
                        <p class="small mt-2"><fmt:message key="footer.boletin.texto"/></p>
                    </div>
                </div>
                <hr class="opacity-25">
                <div class="text-center small">
                    &copy; 2026 <fmt:message key="app.nombre"/> - <fmt:message key="footer.derechos"/>.
                </div>
            </div>
        </footer>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    </body>
</html>