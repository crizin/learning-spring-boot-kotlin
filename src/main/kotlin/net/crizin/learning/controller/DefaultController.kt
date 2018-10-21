package net.crizin.learning.controller

import net.crizin.learning.service.NoteService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DefaultController(
		private val noteService: NoteService
) : AbstractController() {
	@GetMapping("/")
	fun index(model: Model, @PageableDefault(sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable): String {
		model.addAttribute("notes", noteService.getAllNotes(pageable))
		return "index"
	}
}