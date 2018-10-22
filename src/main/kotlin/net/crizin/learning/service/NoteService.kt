package net.crizin.learning.service

import net.crizin.learning.entity.Member
import net.crizin.learning.entity.Note
import net.crizin.learning.entity.Tag
import net.crizin.learning.repository.NoteRepository
import net.crizin.learning.repository.TagRepository
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.DigestUtils
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class NoteService {
	private val logger = LoggerFactory.getLogger(javaClass)
	private val maxTagCount = 10

	@Autowired
	private lateinit var noteRepository: NoteRepository

	@Autowired
	private lateinit var tagRepository: TagRepository

	@Value("\${attachment.path}")
	private lateinit var attachmentPath: String

	@Transactional
	fun upsertNote(note: Note): Note {
		return noteRepository.save(note)
	}

	@Transactional
	fun getNote(noteId: Int): Note? {
		return noteRepository.findById(noteId).orElse(null)
	}

	@Transactional
	fun getTag(tagName: String): Tag? {
		return tagRepository.findByName(tagName)
	}

	@Transactional
	fun getAllNotes(pageable: Pageable): Page<Note> {
		return noteRepository.findAll(pageable)
	}

	@Transactional
	fun getNotesByMember(member: Member, pageable: Pageable): Page<Note> {
		return noteRepository.findByMember(member, pageable)
	}

	@Transactional
	fun getNotesByTag(tag: Tag, pageable: Pageable): Page<Note> {
		return noteRepository.findByTags(tag, pageable)
	}

	@Transactional
	fun deleteNote(note: Note) {
		noteRepository.delete(note)
	}

	@Transactional
	fun convertTags(tags: Sequence<String>): Set<Tag> {
		val filteredTags = tags
				.map { StringUtils.trimToNull(it) }
				.filterNotNull()
				.sorted()
				.take(maxTagCount)
				.toSet()

		val existingTags = tagRepository.findByNameIn(filteredTags)

		val existingTagNames = existingTags
				.asSequence()
				.map { it.name }
				.toSet()

		val newTags = filteredTags
				.asSequence()
				.filterNot { existingTagNames.contains(it) }
				.map { Tag(name = it) }
				.toSortedSet()

		tagRepository.saveAll(newTags)

		newTags.addAll(existingTags)

		return newTags
	}

	fun saveFile(file: MultipartFile): String? {
		val fileName = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().toByteArray())

		return try {
			Files.copy(file.inputStream, Paths.get(attachmentPath, fileName))
			fileName
		} catch (e: IOException) {
			null
		}
	}

	fun deleteFile(imagePath: String) {
		try {
			Files.delete(Paths.get(attachmentPath, imagePath))
		} catch (e: IOException) {
			logger.error("Can't delete attachment file [imagePath=$imagePath]", e)
		}
	}

	fun responseAttachment(noteId: Int, imagePath: String): StreamingResponseBody? {
		if (StringUtils.isBlank(imagePath)) {
			return null
		}

		val note = getNote(noteId) ?: return null

		return if (imagePath != note.imagePath) {
			null
		} else {
			StreamingResponseBody { outputStream -> Files.copy(Paths.get(attachmentPath, imagePath), outputStream) }
		}
	}
}