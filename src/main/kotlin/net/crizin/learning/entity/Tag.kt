package net.crizin.learning.entity

import javax.persistence.*

@Entity
data class Tag(
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		val id: Int = 0,

		@Column(nullable = false, unique = true)
		val name: String
) : Comparable<Tag> {
	override fun compareTo(other: Tag): Int {
		return compareValuesBy(this, other) { it.name }
	}
}