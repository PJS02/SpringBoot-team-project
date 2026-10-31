
package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


	@Bean
	public SecurityFilterChain securityFilterChain(
	        HttpSecurity http) throws Exception {
	
	    http
	        .authorizeHttpRequests(auth -> auth
	            .requestMatchers(
	                "/user/signup",
	                "/css/**",
	                "/js/**",
	                "/images/**"
	            ).permitAll()
	            .anyRequest().authenticated()
	        )

			.formLogin(form -> form
			    .loginPage("/login")
			    .loginProcessingUrl("/login")
			    .defaultSuccessUrl("/user/profile", true)
			    .failureUrl("/login?error")
			    .permitAll()
			)

	        .logout(logout -> logout
	            .logoutSuccessUrl("/login?logout")
	            .invalidateHttpSession(true)
	            .clearAuthentication(true)
	            .permitAll()
	        )
	        .sessionManagement(session -> session
	            .sessionFixation(fixation ->
	                fixation.changeSessionId()
	            )
	        );
	
	    return http.build();
	}

}
