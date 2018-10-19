package net.crizin.learning.entity

import javax.persistence.*

@Entity
data class Tag(
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		val id: Int = 0,

		@Column(nullable = false, unique = true)
		val name: String
)