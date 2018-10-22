package net.crizin.learning.repository

import net.crizin.learning.entity.Member
import net.crizin.learning.entity.Note
import net.crizin.learning.entity.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository : JpaRepository<Note, Int> {
	fun findByMember(member: Member, pageable: Pageable): Page<Note>

	fun findByTags(tag: Tag, pageable: Pageable): Page<Note>
}