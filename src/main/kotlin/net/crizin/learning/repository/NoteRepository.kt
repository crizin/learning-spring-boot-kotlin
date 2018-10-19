package net.crizin.learning.repository

import net.crizin.learning.entity.Note
import org.springframework.data.jpa.repository.JpaRepository

interface NoteRepository : JpaRepository<Note, Int>