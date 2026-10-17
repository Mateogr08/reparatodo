package com.proyecto.backend.security;

import com.proyecto.backend.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    public AuthFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || esPublica(request)) {
                filterChain.doFilter(request, response);
                return;
            }

            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                noAutenticado(response);
                return;
            }

            String token = header.substring(7);
            var usuario = usuarioRepository.findByToken(token);
            if (usuario.isEmpty()) {
                noAutenticado(response);
                return;
            }

            SesionActual.set(usuario.get());
            filterChain.doFilter(request, response);
        } finally {
            SesionActual.clear();
        }
    }

    private boolean esPublica(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/api/auth/login") || path.startsWith("/error");
    }

    private void noAutenticado(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\":\"Debe iniciar sesión\"}");
    }
}
