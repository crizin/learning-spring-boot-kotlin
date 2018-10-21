package net.crizin.learning.service

import net.crizin.learning.entity.Member
import net.crizin.learning.repository.MemberRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class MemberService(
		private val memberRepository: MemberRepository,
		private val passwordEncoder: PasswordEncoder
) {
	fun createMember(userName: String, password: String): Member {
		return memberRepository.saveAndFlush(Member(
				userName = userName,
				password = passwordEncoder.encode(password)
		))

	}
}