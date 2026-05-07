<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="${sessionScope.lang != null ? sessionScope.lang : 'es'}" scope="session" />
<fmt:setBundle basename="resources/messages" scope="session" />
<nav class="navbar navbar-expand-lg navbar-salud sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/index.jsp">
            <i class="bi bi-heart-pulse me-2"></i> <fmt:message key="app.nombre"/>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarMain">
            <ul class="navbar-nav ms-auto mb-2 mb-lg-0">
                <c:choose>
                    <c:when test="${sessionScope.rol == 'RECEPCIONISTA'}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard"><fmt:message key="nav.dashboard"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/PacienteServlet?accion=listar"><fmt:message key="nav.pacientes"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/CitaServlet?accion=listar"><fmt:message key="nav.citas"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/HorarioServlet?accion=listar"><fmt:message key="nav.horarios"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/UsuarioServlet?accion=listar"><fmt:message key="nav.empleados"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/log-auditoria"><fmt:message key="nav.auditoria"/></a></li>
                    </c:when>
                    <c:when test="${sessionScope.rol == 'MEDICO'}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard"><fmt:message key="nav.dashboard"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/CitaServlet?accion=listar"><fmt:message key="nav.mis_citas"/></a></li>
                    </c:when>
                    <c:when test="${sessionScope.rol == 'ENFERMERO'}">
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/dashboard"><fmt:message key="nav.dashboard"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/CitaServlet?accion=listar"><fmt:message key="nav.citas"/></a></li>
                        <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/PacienteServlet?accion=listar"><fmt:message key="nav.pacientes"/></a></li>
                    </c:when>
                    <c:otherwise>
                        <!-- Usuario no autenticado o rol desconocido: no mostrar enlaces -->
                    </c:otherwise>
                </c:choose>
            </ul>
            <div class="d-flex align-items-center gap-2">
                <div class="dropdown">
                    <button class="btn btn-outline-light btn-sm dropdown-toggle" type="button" data-bs-toggle="dropdown">
                        <i class="bi bi-globe2"></i> <c:out value="${sessionScope.lang eq 'en' ? 'EN' : 'ES'}"/>
                    </button>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li><a class="dropdown-item" href="?lang=es"><fmt:message key="app.lang.es"/></a></li>
                        <li><a class="dropdown-item" href="?lang=en"><fmt:message key="app.lang.en"/></a></li>
                    </ul>
                </div>
                <c:choose>
                    <c:when test="${not empty sessionScope.usuario}">
                        <div class="dropdown">
                            <button class="btn btn-outline-light btn-sm dropdown-toggle" data-bs-toggle="dropdown">
                                <i class="bi bi-person-circle"></i> ${sessionScope.usuario.nombres}
                            </button>
                            <ul class="dropdown-menu dropdown-menu-end">
                                <li><a class="dropdown-item" href="${pageContext.request.contextPath}/logout"><fmt:message key="nav.salir"/></a></li>
                            </ul>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/views/login.jsp" class="btn btn-outline-light btn-sm"><fmt:message key="nav.acceso"/></a>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</nav>