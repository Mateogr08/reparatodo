package com.proyecto.backend.config;

import com.proyecto.backend.repository.UsuarioRepository;
import com.proyecto.backend.security.AuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FiltroAuthConfig {

    @Bean
    public FilterRegistrationBean<AuthFilter> registroAuthFilter(UsuarioRepository usuarioRepository) {
        FilterRegistrationBean<AuthFilter> registro = new FilterRegistrationBean<>(new AuthFilter(usuarioRepository));
        registro.addUrlPatterns("/api/*");
        registro.setOrder(1);
        return registro;
    }
}
