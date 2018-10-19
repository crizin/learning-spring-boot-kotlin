package net.crizin.learning

import nz.net.ultraq.thymeleaf.LayoutDialect
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.thymeleaf.TemplateEngine
import org.thymeleaf.spring5.SpringTemplateEngine

@SpringBootApplication
@EnableJpaRepositories
@EnableTransactionManagement
class Application {
	@Bean
	fun templateEngine(): TemplateEngine {
		val templateEngine = SpringTemplateEngine()
		templateEngine.addDialect(LayoutDialect())
		return templateEngine
	}
}

fun main(args: Array<String>) {
	runApplication<Application>(*args)
}