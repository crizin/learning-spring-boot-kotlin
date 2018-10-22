package net.crizin.learning.controller

import net.crizin.learning.AbstractControllerTest
import net.crizin.learning.entity.Note
import net.crizin.learning.security.AuthenticationUser
import org.apache.commons.lang3.math.NumberUtils
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@SpringBootTest
@RunWith(SpringRunner::class)
class DefaultControllerTest : AbstractControllerTest() {
	@Test
	fun testIndex() {
		var mvcResult = mockMvc.perform(get("/"))
				.andExpect(status().isOk)
				.andExpect(model().attributeExists("notes"))
				.andReturn()

		var modelMap = mvcResult.modelAndView!!.modelMap
		@Suppress("UNCHECKED_CAST")
		val emptyNotes = modelMap["notes"] as Page<Note>
		assertEquals(0, emptyNotes.totalElements)

		val note = noteService.upsertNote(createDummyNote(createDummyMember()))

		mvcResult = mockMvc.perform(get("/"))
				.andExpect(status().isOk)
				.andExpect(model().attributeExists("notes"))
				.andReturn()

		modelMap = mvcResult.modelAndView!!.modelMap
		@Suppress("UNCHECKED_CAST")
		val oneNote = modelMap["notes"] as Page<Note>
		assertEquals(1, oneNote.totalElements)
		assertEquals(note.id.toLong(), oneNote.content[0].id.toLong())
		assertEquals(note.content, oneNote.content[0].content)

	}

	@Test
	@Throws(Exception::class)
	fun testNote() {
		val member = memberService.createMember(userName, password)
		val user = AuthenticationUser(member)

		var mvcResult = mockMvc.perform(get("/note")
				.with(user(user)))
				.andExpect(status().isOk)
				.andExpect(model().attributeExists("notes"))
				.andReturn()

		var modelMap = mvcResult.modelAndView!!.modelMap
		@Suppress("UNCHECKED_CAST")
		val emptyNotes = modelMap["notes"] as Page<Note>
		assertEquals(0, emptyNotes.totalElements)

		val note = noteService.upsertNote(createDummyNote(member))

		mvcResult = mockMvc.perform(get("/note")
				.with(user(user)))
				.andExpect(status().isOk)
				.andExpect(model().attributeExists("notes"))
				.andReturn()

		modelMap = mvcResult.modelAndView!!.modelMap
		@Suppress("UNCHECKED_CAST")
		val oneNote = modelMap["notes"] as Page<Note>
		assertEquals(1, oneNote.totalElements)
		assertEquals(note.id.toLong(), oneNote.content[0].id.toLong())
		assertEquals(note.content, oneNote.content[0].content)
	}

	@Test
	@Throws(Exception::class)
	fun testWriteNote() {
		val member = memberService.createMember(userName, password)
		val user = AuthenticationUser(member)

		val mvcResult = mockMvc.perform(fileUpload("/note")
				.file(MockMultipartFile("file", "test.jpg", MediaType.IMAGE_JPEG_VALUE, byteArrayOf(0)))
				.with(csrf())
				.with(user(user))
				.param("id", "0")
				.param("title", "test title")
				.param("content", "test content")
				.param("tags[]", "Tag1")
				.param("tags[]", "Tag2")
				.param("tags[]", "Tag3"))
				.andExpect(status().isFound)
				.andExpect(redirectedUrlPattern("/note/*"))
				.andReturn()

		val note = noteService.getNote(NumberUtils.toInt(mvcResult.response.redirectedUrl!!.replaceFirst("^/note/".toRegex(), "")))!!

		assertNotNull(note)
		assertEquals("test title", note.title)
		assertEquals("test content", note.content)
		assertEquals("Tag1, Tag2, Tag3", note.getTagString())
		assertTrue(note.imagePath!!.matches("^[a-f\\d]{32}$".toRegex()))
	}

	@Test
	@Throws(Exception::class)
	fun testEditNote() {
		val member = memberService.createMember(userName, password)
		val user = AuthenticationUser(member)

		val note = noteService.upsertNote(createDummyNote(member))

		mockMvc.perform(multipart("/note")
				.file(MockMultipartFile("file", "", MediaType.APPLICATION_OCTET_STREAM_VALUE, byteArrayOf()))
				.with(csrf())
				.with(user(user))
				.param("id", note.id.toString())
				.param("title", "modified title")
				.param("content", "modified content")
				.param("tags[]", "TagA")
				.param("tags[]", "TagB")
				.param("tags[]", "TagC")
				.param("removeFile", "1"))
				.andExpect(status().isFound)
				.andExpect(redirectedUrlPattern("/note/*"))
				.andReturn()

		val modifiedNote = noteService.getNote(note.id)!!

		assertNotNull(note)
		assertEquals("modified title", modifiedNote.title)
		assertEquals("modified content", modifiedNote.content)
		assertEquals("TagA, TagB, TagC", modifiedNote.getTagString())
		assertNull(modifiedNote.imagePath)
	}

	@Test
	@Throws(Exception::class)
	fun testDeleteNote() {
		val member = memberService.createMember(userName, password)
		val user = AuthenticationUser(member)

		val (id) = noteService.upsertNote(createDummyNote(member))

		mockMvc.perform(delete(String.format("/note/%d", id))
				.with(csrf())
				.with(user(user)))
				.andExpect(status().isOk)
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(jsonPath("$.success").value(true))
				.andReturn()

		assertNull(noteService.getNote(id))

		mockMvc.perform(get("/note/$id")
				.with(user(user)))
				.andExpect(status().isNotFound)
	}
}