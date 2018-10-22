package net.crizin.learning.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class UnknownResourceException(message: String) : RuntimeException(message)