package com.liceu.server.domain.global

import java.lang.Exception

open class ItemNotFoundException : Exception()
class QuestionNotFoundException : ItemNotFoundException()
class TagNotFoundException : ItemNotFoundException()

open class AlreadyExistsException : Exception()
class TagAlreadyExistsException : AlreadyExistsException()

class InputValidationException : Exception()