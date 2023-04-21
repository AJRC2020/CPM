package org.feup.apm.acme

class KeyException(message:String) : Exception(message)
class GenerateKeysException(message: String) : Exception(message)
class CreateQRCodeException(message: String) : Exception(message)
class SigningException(message: String) : Exception(message)

class ServerError(message: String) : Exception(message)

class ElementAlreadyInUse(message: String) : Exception(message)

class ConnectionError(message: String) : Exception(message)

class Forbidden(message: String) : Exception(message)

class NotFound(message: String) : Exception(message)