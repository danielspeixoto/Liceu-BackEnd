package com.liceu.server.domain.global

import java.lang.Exception
import javax.validation.ValidationException

open class ItemNotFoundException(msg: String="") : Exception(msg)
class QuestionNotFoundException(msg: String="") : ItemNotFoundException(msg)
class TagNotFoundException(msg: String="") : ItemNotFoundException(msg)

open class AlreadyExistsException(msg: String="") : Exception(msg)
class TagAlreadyExistsException(msg: String="") : AlreadyExistsException(msg)

class InputValidationException(msg: String="") : Exception(msg)

open class AuthenticationException(msg: String="") : Exception(msg)
class AccessTokenException(msg: String="") : AuthenticationException(msg)


open class OverflowSizeException(msg: String="") : ValidationException(msg)
class OverflowSizeTagsException(msg: String="") : OverflowSizeException(msg)
class OverflowSizeMessageException(msg: String="") : OverflowSizeException(msg)

open class TypeMismatchException(msg: String="") : Exception(msg)

