package net.crizin.learning.security

import net.crizin.learning.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class AuthenticationService : UserDetailsService {
	@Autowired
	private lateinit var memberRepository: MemberRepository

	override fun loadUserByUsername(username: String): UserDetails {
		return AuthenticationUser(memberRepository.findByUserName(username) ?: throw UsernameNotFoundException(username))
	}
}