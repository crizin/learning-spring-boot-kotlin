package net.crizin.learning.service

import net.crizin.learning.entity.Member
import net.crizin.learning.repository.MemberRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class MemberService {
	@Autowired
	private lateinit var memberRepository: MemberRepository

	@Autowired
	private lateinit var passwordEncoder: PasswordEncoder

	fun createMember(userName: String, password: String): Member =
			memberRepository.saveAndFlush(Member(
					userName = userName,
					password = passwordEncoder.encode(password)
			))
}