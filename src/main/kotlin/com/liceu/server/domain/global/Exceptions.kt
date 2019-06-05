package com.liceu.server.domain.global

import java.lang.Exception

open class ItemNotFoundException(msg: String="") : Exception(msg)
class QuestionNotFoundException(msg: String="") : ItemNotFoundException(msg)
class TagNotFoundException(msg: String="") : ItemNotFoundException(msg)

open class AlreadyExistsException(msg: String="") : Exception(msg)
class TagAlreadyExistsException(msg: String="") : AlreadyExistsException(msg)

class InputValidationException(msg: String="") : Exception(msg)

open class AuthenticationException(msg: String="") : Exception(msg)
class AccessTokenException(msg: String="") : AuthenticationException(msg)