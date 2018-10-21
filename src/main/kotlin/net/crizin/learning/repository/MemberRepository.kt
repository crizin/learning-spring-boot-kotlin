package net.crizin.learning.repository

import net.crizin.learning.entity.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Int> {
	fun findByUserName(userName: String): Member?
}