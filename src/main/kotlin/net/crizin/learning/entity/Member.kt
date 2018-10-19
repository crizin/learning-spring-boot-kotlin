package net.crizin.learning.entity

import java.time.LocalDateTime
import javax.persistence.*

@Entity
data class Member(
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		val id: Int = 0,

		@Column(nullable = false, unique = true)
		val userName: String,

		@Column(nullable = false)
		val password: String,

		@Column(nullable = false)
		val createdAt: LocalDateTime = LocalDateTime.now()
)