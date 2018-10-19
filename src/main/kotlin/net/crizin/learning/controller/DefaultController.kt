package net.crizin.learning.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class DefaultController {
	@GetMapping("/")
	fun index(model: Model): String {
		model.addAttribute("value", System.currentTimeMillis())
		return "index"
	}
}