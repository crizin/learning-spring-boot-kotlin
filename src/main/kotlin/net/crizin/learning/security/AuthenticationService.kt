package net.crizin.learning.security

import net.crizin.learning.repository.MemberRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class AuthenticationService(
		private val memberRepository: MemberRepository
) : UserDetailsService {
	override fun loadUserByUsername(username: String): UserDetails {
		return AuthenticationUser(memberRepository.findByUserName(username) ?: throw UsernameNotFoundException(username))
	}
}