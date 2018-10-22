package net.crizin.learning.entity

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
data class Note(
		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		val id: Int = 0,

		@ManyToOne(optional = false)
		val member: Member,

		@Column
		val title: String? = null,

		@Lob
		@Column(nullable = false)
		val content: String = "",

		@OrderBy("name ASC")
		@ManyToMany(fetch = FetchType.EAGER)
		val tags: Set<Tag> = TreeSet(),

		val imagePath: String? = null,

		@Column(nullable = false)
		val createdAt: LocalDateTime = LocalDateTime.now(),

		val updatedAt: LocalDateTime? = null
) {
	fun isWriter(member: Member?): Boolean {
		return member?.id == this.member.id
	}

	fun getTagString(): String = tags.asSequence().map { it.name }.sorted().joinToString(", ")
}