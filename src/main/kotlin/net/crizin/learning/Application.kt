package net.crizin.learning

import net.crizin.learning.interceptor.AuthenticateInterceptor
import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring5.SpringTemplateEngine

@SpringBootApplication
@EnableWebSecurity
@EnableJpaRepositories
@EnableTransactionManagement
class Application : WebMvcConfigurer {
	@Bean
	fun templateEngine(): TemplateEngine =
			SpringTemplateEngine().apply {
				addDialect(LayoutDialect())
			}

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry
				.addInterceptor(AuthenticateInterceptor())
				.addPathPatterns("/**")
				.excludePathPatterns("/sign-up")
				.excludePathPatterns("/log-in")
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}