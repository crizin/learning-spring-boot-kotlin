package net.crizin.learning.controller

import net.crizin.learning.entity.Member
import net.crizin.learning.security.AuthenticationUser
import net.crizin.learning.service.MemberService
import org.apache.commons.lang3.StringUtils
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest

@Controller
class AuthenticateController(
		private val authenticationManager: AuthenticationManager,
		private val memberService: MemberService
) : AbstractController() {
	@GetMapping("/log-in")
	fun logIn(model: Model, @RequestParam(required = false) error: String?): String {
		if (error != null) {
			model.addAttribute("error", "Invalid user name or password.")
		}

		return "authenticate/log-in"
	}

	@GetMapping("/sign-up")
	fun signUp(): String {
		return "authenticate/sign-up"
	}

	@PostMapping("/sign-up")
	fun signUp(request: HttpServletRequest, model: Model, @RequestParam userName: String, @RequestParam password1: String, @RequestParam password2: String): String {
		if (StringUtils.isAnyBlank(userName, password1, password2)) {
			throw IllegalStateException()
		}

		if (password1 != password2) {
			model.addAttribute("userName", userName)
			model.addAttribute("error", "Password does not match the confirm password.")
			return "authenticate/sign-up"
		}

		val member: Member

		try {
			member = memberService.createMember(userName, password1)
		} catch (e: DataIntegrityViolationException) {
			model.addAttribute("error", "User name already taken.")
			return "authenticate/sign-up"
		}

		val authenticationUser = AuthenticationUser(member)

		val token = UsernamePasswordAuthenticationToken(member.userName, password1, authenticationUser.authorities)
		authenticationManager.authenticate(token)

		if (token.isAuthenticated) {
			SecurityContextHolder.getContext().authentication = authenticationManager.authenticate(token)
		}

		return "redirect:/note"
	}
}