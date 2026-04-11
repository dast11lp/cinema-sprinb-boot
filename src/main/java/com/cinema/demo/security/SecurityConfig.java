package com.cinema.demo.security;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableMethodSecurity(securedEnabled = true ,prePostEnabled = true)
public class SecurityConfig {
	
	@Autowired private JWTFilter filter;
	
	private Logger log = LoggerFactory.getLogger(getClass());
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		
		
// antes
/*		http.csrf().disable()
			.httpBasic().disable()
			.cors()
			.and()
			.authorizeHttpRequests()
			.antMatchers("/auth/**").permitAll()
//			.anyRequest().authenticated()
			.and()
			.exceptionHandling()
				.authenticationEntryPoint(null)
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);*/

		http
				.cors( cors -> cors.disable()) //deshabilita cors
				.csrf(csrf-> csrf.disable()) //deshabilita csrf
						.httpBasic(basic-> basic.disable()) //hace que spring no espere la cabecera Authorization: Basic
								.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**") //rutas de accesp público
										.permitAll().anyRequest().authenticated() // rutas que requieren autenticación
								)
										.exceptionHandling(ex -> ex.authenticationEntryPoint(null)) // ??
												.sessionManagement(session -> session
														.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // no crear sesiones en el navegador


		
		http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
		
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
		return authConfig.getAuthenticationManager();
	}
	
}
