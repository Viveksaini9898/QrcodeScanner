package com.qr.scanner.model

import com.qr.scanner.extension.startsWithAnyIgnoreCase


class Youtube(val url: String) : Parsers {

    companion object {
        private val PREFIXES = listOf("vnd.youtube://", "http://www.youtube.com", "https://www.youtube.com")

        fun parse(text: String): Youtube? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            return Youtube(text)
        }
    }

    override val parser = ParsedResultType.YOUTUBE
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url
}