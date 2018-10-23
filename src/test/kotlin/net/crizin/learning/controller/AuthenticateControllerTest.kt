package net.crizin.learning.controller

import net.crizin.learning.AbstractControllerTest
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated
import org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@RunWith(SpringRunner::class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthenticateControllerTest : AbstractControllerTest() {
	@Test
	@Throws(Exception::class)
	fun testSignUpSuccess() {
		mockMvc
				.perform(post("/sign-up")
						.with(csrf())
						.param("userName", userName)
						.param("password1", password)
						.param("password2", password))
				.andExpect(authenticated().withUsername(userName))
				.andExpect(status().isFound)
				.andExpect(redirectedUrl("/note"))
	}

	@Test
	@Throws(Exception::class)
	fun testSignUpFailedByDuplicateUserName() {
		memberService.createMember(userName, password)

		mockMvc
				.perform(post("/sign-up")
						.with(csrf())
						.param("userName", userName)
						.param("password1", password)
						.param("password2", password))
				.andExpect(unauthenticated())
				.andExpect(status().isOk)
				.andExpect(model().attribute("error", "User name already taken."))
	}

	@Test
	@Throws(Exception::class)
	fun testSignUpFailedByMisMatchingConfirmPassword() {
		mockMvc
				.perform(post("/sign-up")
						.with(csrf())
						.param("userName", userName)
						.param("password1", password)
						.param("password2", "invalid-password"))
				.andExpect(unauthenticated())
				.andExpect(status().isOk)
				.andExpect(model().attribute("error", "Password does not match the confirm password."))
	}

	@Test
	@Throws(Exception::class)
	fun testLoginSuccess() {
		memberService.createMember(userName, password)

		mockMvc.perform(formLogin("/log-in-process")
				.user("userName", userName)
				.password("password", password))
				.andExpect(authenticated().withUsername(userName))
				.andExpect(status().isFound)
				.andExpect(redirectedUrl("/"))
	}

	@Test
	@Throws(Exception::class)
	fun testLoginFailed() {
		mockMvc.perform(formLogin("/log-in-process")
				.user("userName", userName)
				.password("password", password))
				.andExpect(unauthenticated())
				.andExpect(status().isFound)
				.andExpect(redirectedUrl("/log-in?error"))
	}

	@Test
	@Throws(Exception::class)
	fun testLogout() {
		mockMvc.perform(logout("/log-out"))
				.andExpect(status().isFound)
				.andExpect(redirectedUrl("/"))
	}
}