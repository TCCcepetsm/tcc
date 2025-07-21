package com.recorder.config;

import com.recorder.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	// Lista de origens permitidas (incluindo possíveis ambientes)
	private static final List<String> ALLOWED_ORIGINS = Arrays.asList(
			"http://localhost:5000",
			"http://127.0.0.1:5000",
			"http://localhost:3000",
			"https://seu-site-production.com" // Adicione seu domínio de produção aqui
	);

	// Endpoints públicos que não requerem autenticação
	private static final String[] PUBLIC_ENDPOINTS = {
			"/api/auth/authenticate",
			"/api/auth/registrar", // Corrigido para match com o frontend
			"/api/usuarios/**",
			"/swagger-ui/**",
			"/v3/api-docs/**",
			"/api-docs/**"
	};

	private final CustomUserDetailsService userDetailsService;
	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(CustomUserDetailsService userDetailsService, JwtAuthFilter jwtAuthFilter) {
		this.userDetailsService = userDetailsService;
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http
				.cors(cors -> cors.configurationSource(corsConfigurationSource()))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth
						// Rotas públicas
						.requestMatchers(PUBLIC_ENDPOINTS).permitAll()

						// Validação de token
						.requestMatchers("/api/auth/validate-token").authenticated()

						// Rotas específicas com controle de acesso
						.requestMatchers("/api/agendamentos/**")
						.hasAnyAuthority("ROLE_USUARIO", "ROLE_PROFISSIONAL", "ROLE_ADMIN")
						.requestMatchers("/api/profissional/**")
						.hasAuthority("ROLE_PROFISSIONAL")
						.requestMatchers("/api/admin/**")
						.hasAuthority("ROLE_ADMIN")

						// Todas as outras requisições
						.anyRequest().authenticated())
				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(ALLOWED_ORIGINS);
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList(
				"Authorization",
				"Content-Type",
				"Accept",
				"X-Requested-With",
				"Cache-Control"));
		config.setExposedHeaders(Arrays.asList("Authorization"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
}