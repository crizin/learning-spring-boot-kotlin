package net.crizin.learning.controller

import net.crizin.learning.entity.Member
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

abstract class AbstractController {
	protected fun getCurrentMember(request: HttpServletRequest): Member = request.getAttribute("currentMember") as Member

	protected fun getErrorModelAndView(response: HttpServletResponse, httpStatus: HttpStatus, errorMessage: String): ModelAndView {
		response.status = httpStatus.value()

		val modelAndView = ModelAndView()
		modelAndView.addObject("errorTitle", "${httpStatus.value()} ${httpStatus.reasonPhrase}")
		modelAndView.addObject("errorDetails", errorMessage)
		modelAndView.viewName = "error"

		return modelAndView
	}

	protected fun respondJsonOk(): Map<String, Any> = mapOf("success" to true)

	protected fun respondJsonError(message: String): Map<String, Any> = mapOf("success" to false, "message" to message)
}