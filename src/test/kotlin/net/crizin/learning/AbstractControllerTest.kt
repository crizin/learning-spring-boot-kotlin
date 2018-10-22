package net.crizin.learning

import net.crizin.learning.entity.Member
import net.crizin.learning.entity.Note
import net.crizin.learning.service.MemberService
import net.crizin.learning.service.NoteService
import org.junit.Before
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.util.DigestUtils
import org.springframework.web.context.WebApplicationContext
import java.util.*
import javax.servlet.Filter

@Component
class AbstractControllerTest {
	protected val userName = "testUserName"
	protected val password = "testPassword"

	protected lateinit var mockMvc: MockMvc
	@Autowired
	protected lateinit var webApplicationContext: WebApplicationContext
	@Autowired
	protected lateinit var springSecurityFilterChain: Filter
	@Autowired
	protected lateinit var memberService: MemberService
	@Autowired
	protected lateinit var noteService: NoteService

	@Before
	fun setUp() {
		mockMvc = MockMvcBuilders
				.webAppContextSetup(webApplicationContext)
				.addFilter<DefaultMockMvcBuilder>(springSecurityFilterChain)
				.build()
	}

	protected fun createDummyMember(): Member = memberService.createMember(UUID.randomUUID().toString(), UUID.randomUUID().toString())

	protected fun createDummyNote(member: Member): Note = Note(
			member = member,
			title = "Dummy title",
			content = "Dummy content",
			tags = noteService.convertTags(setOf("Tag1", "Tag2", "Tag3").asSequence()),
			imagePath = DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().toByteArray())
	)
}