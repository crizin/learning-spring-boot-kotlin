package net.crizin.learning.controller

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.error.ErrorAttributes
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.NoHandlerFoundException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Controller
@ControllerAdvice
class ErrorController : AbstractController(), ErrorController {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Autowired
	private lateinit var errorAttributes: ErrorAttributes

	@ExceptionHandler(Exception::class)
	fun handleError(request: HttpServletRequest, response: HttpServletResponse, exception: Exception): ModelAndView {
		logger.error("Error: ${request.requestURL}", exception)

		val responseStatus: HttpStatus

		val annotation = AnnotationUtils.findAnnotation(exception.javaClass, ResponseStatus::class.java)

		responseStatus = annotation?.code ?: if (exception is NoHandlerFoundException) {
			HttpStatus.NOT_FOUND
		} else {
			HttpStatus.INTERNAL_SERVER_ERROR
		}

		return getErrorModelAndView(response, responseStatus, exception.message)
	}

	@RequestMapping("/error")
	fun error(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {
		val errorAttributes = errorAttributes.getErrorAttributes(ServletWebRequest(request), true)

		logger.error("Error: $errorAttributes")

		return getErrorModelAndView(
				response,
				HttpStatus.valueOf(errorAttributes.getOrDefault("status", HttpStatus.INTERNAL_SERVER_ERROR) as Int),
				errorAttributes.getOrDefault("message", "Internal server error") as String
		)
	}

	override fun getErrorPath(): String = "/error"
}