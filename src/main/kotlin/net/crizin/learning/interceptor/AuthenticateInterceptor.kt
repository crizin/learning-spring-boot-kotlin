package net.crizin.learning.interceptor

import net.crizin.learning.security.AuthenticationUser
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthenticateInterceptor : HandlerInterceptorAdapter() {
	@Throws(Exception::class)
	override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
		val session = request.getSession(false)

		if (session != null) {
			val authentication = SecurityContextHolder.getContext().authentication
			if (authentication?.principal is AuthenticationUser) {
				val authenticationUser = authentication.principal as AuthenticationUser
				request.setAttribute("currentMember", authenticationUser.member)
			}
		}

		return super.preHandle(request, response, handler)
	}
}