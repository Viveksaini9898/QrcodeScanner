package com.qr.scanner.model

import com.qr.scanner.extension.joinToStringNotNullOrBlankWithLineSeparator
import com.qr.scanner.extension.removePrefixIgnoreCase
import com.qr.scanner.extension.startsWithIgnoreCase

data class Sms(
    val phone: String? = null,
    val message: String? = null
) : Parsers {

    companion object {
        private const val PREFIX = "smsto:"
        private const val SEPARATOR = ":"

        fun parse(text: String): Sms? {
            if (text.startsWithIgnoreCase(PREFIX).not()) {
                return null
            }

            val parts = text.removePrefixIgnoreCase(PREFIX).split(SEPARATOR)

            return Sms(
                phone = parts.getOrNull(0),
                message = parts.getOrNull(1)
            )
        }
    }

    override val parser = ParsedResultType.SMS

    override fun toFormattedText(): String {
        return listOf(phone, message).joinToStringNotNullOrBlankWithLineSeparator()
    }

    override fun toBarcodeText(): String {
        return PREFIX +
                phone.orEmpty() +
                "$SEPARATOR${message.orEmpty()}"
    }
}