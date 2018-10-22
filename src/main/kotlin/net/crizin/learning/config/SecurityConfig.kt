package net.crizin.learning.config

import net.crizin.learning.security.AuthenticationService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
class SecurityConfig : WebSecurityConfigurerAdapter() {
	@Bean
	fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

	@Bean
	fun userDetailService(): UserDetailsService = AuthenticationService()

	@Bean
	fun authenticationProvider(): AuthenticationProvider =
			DaoAuthenticationProvider().apply {
				setPasswordEncoder(passwordEncoder())
				setUserDetailsService(userDetailsService())
			}

	@Bean
	@Throws(Exception::class)
	override fun authenticationManager(): AuthenticationManager = super.authenticationManager()

	@Throws(Exception::class)
	override fun configure(auth: AuthenticationManagerBuilder) {
		auth
				.userDetailsService(userDetailService())
				.passwordEncoder(passwordEncoder())
	}

	@Throws(Exception::class)
	override fun configure(web: WebSecurity) {
		web
				.ignoring()
				.antMatchers("/static/**")
	}

	@Throws(Exception::class)
	override fun configure(http: HttpSecurity) {
		with(http) {
			authorizeRequests()
					.antMatchers("/note/**").hasRole("USER")
					.antMatchers("/**").permitAll()
					.anyRequest().authenticated()

			formLogin()
					.loginPage("/log-in")
					.loginProcessingUrl("/log-in-process")
					.usernameParameter("userName")
					.passwordParameter("password")

			logout()
					.logoutUrl("/log-out")
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
		}
	}
}