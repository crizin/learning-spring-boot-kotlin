package net.crizin.learning.service

import net.crizin.learning.entity.Note
import net.crizin.learning.repository.NoteRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class NoteService(
		private val noteRepository: NoteRepository
) {
	fun getAllNotes(pageable: Pageable): Page<Note> = noteRepository.findAll(pageable)
}