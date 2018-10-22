package net.crizin.learning.repository

import net.crizin.learning.entity.Tag
import org.springframework.data.jpa.repository.JpaRepository

interface TagRepository : JpaRepository<Tag, Int> {
	fun findByName(name: String): Tag?

	fun findByNameIn(names: Iterable<String>): Set<Tag>
}