package net.crizin.learning.controller

import net.crizin.learning.entity.Note
import net.crizin.learning.exception.UnauthorizedOperation
import net.crizin.learning.exception.UnknownResourceException
import net.crizin.learning.service.NoteService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.time.LocalDateTime
import javax.servlet.http.HttpServletRequest

@Controller
class DefaultController(
		private val noteService: NoteService
) : AbstractController() {
	@GetMapping("/")
	fun index(model: Model, @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable): String {
		model.addAttribute("notes", noteService.getAllNotes(pageable))

		return "index"
	}

	@GetMapping("/note")
	fun list(request: HttpServletRequest, model: Model, @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable): String {
		val currentMember = getCurrentMember(request)

		model.addAttribute("notes", noteService.getNotesByMember(currentMember, pageable))

		return "note/list"
	}

	@GetMapping("/note/write")
	fun write(request: HttpServletRequest, model: Model): String {
		model.addAttribute("note", Note(member = getCurrentMember(request)))

		return "note/write"
	}

	@GetMapping("/note/{noteId}/edit")
	fun edit(request: HttpServletRequest, model: Model, @PathVariable noteId: Int): String {
		val currentMember = getCurrentMember(request)

		val note = noteService.getNote(noteId) ?: throw UnknownResourceException("Not found note [id=$noteId].")

		if (!note.isWriter(currentMember)) {
			throw UnauthorizedOperation()
		}

		model.addAttribute("note", note)

		return "note/write"
	}

	@GetMapping("/note/{noteId}")
	fun view(request: HttpServletRequest, model: Model, @PathVariable noteId: Int): String {
		val currentMember = getCurrentMember(request)

		val note = noteService.getNote(noteId) ?: throw UnknownResourceException("Not found note [id=$noteId].")

		model.addAttribute("note", note)
		model.addAttribute("isMyNote", note.isWriter(currentMember))

		return "note/view"
	}

	@GetMapping("/note/{noteId}/attachment/{imagePath}")
	fun viewAttachment(@PathVariable noteId: Int, @PathVariable imagePath: String): StreamingResponseBody {
		return noteService.responseAttachment(noteId, imagePath) ?: throw UnknownResourceException("Not found attachment [id=$noteId, imagePath=$imagePath].")
	}

	@PostMapping("/note")
	fun postNote(request: HttpServletRequest, redirectAttributes: RedirectAttributes,
				 @RequestParam id: Int, @RequestParam title: String, @RequestParam content: String,
				 @RequestParam file: MultipartFile, @RequestParam(defaultValue = "0") removeFile: Boolean,
				 @RequestParam(name = "tags[]", required = false) tags: Array<String>?): String {

		var note: Note
		val currentMember = getCurrentMember(request)

		if (id == 0) {
			note = Note(member = getCurrentMember(request))
		} else {
			note = noteService.getNote(id) ?: throw UnknownResourceException("Not found note [id=$id].")

			if (!note.isWriter(currentMember)) {
				throw UnauthorizedOperation()
			}

			note = note.copy(updatedAt = LocalDateTime.now())
		}

		note = note.copy(title = title.trim(), content = content.trim())

		if (tags == null) {
			note = note.copy(tags = sortedSetOf())
		} else {
			note = note.copy(tags = noteService.convertTags(tags.asSequence()))
		}

		if (removeFile) {
			noteService.deleteFile(note.imagePath!!)
			note = note.copy(imagePath = null)
		}

		if (!file.isEmpty) {
			if (note.imagePath != null) {
				noteService.deleteFile(note.imagePath!!)
			}

			val imagePath = noteService.saveFile(file)

			if (imagePath == null) {
				redirectAttributes.addFlashAttribute("note", note)
				redirectAttributes.addFlashAttribute("error", "Failed to upload file.")
				return "redirect:/note/write"
			}

			note = note.copy(imagePath = imagePath)
		}

		noteService.upsertNote(note)

		return "redirect:/note/${note.id}"
	}

	@ResponseBody
	@DeleteMapping("/note/{noteId}")
	fun deleteNote(request: HttpServletRequest, @PathVariable noteId: Int): Map<String, Any> {
		val currentMember = getCurrentMember(request)

		val note = noteService.getNote(noteId) ?: return respondJsonError("Not found note [id=$noteId].")

		if (!note.isWriter(currentMember)) {
			return respondJsonError("Invalid request.")
		}

		noteService.deleteNote(note)

		return respondJsonOk()
	}

	@GetMapping("/tag/{tagName}")
	fun listByTag(model: Model, @PathVariable tagName: String, @PageableDefault(sort = arrayOf("id"), direction = Sort.Direction.DESC) pageable: Pageable): String {
		val tag = noteService.getTag(tagName) ?: throw UnknownResourceException("Not found tag [name=$tagName].")

		model.addAttribute("tag", tag)
		model.addAttribute("notes", noteService.getNotesByTag(tag, pageable))

		return "note/list-by-tag"
	}
}