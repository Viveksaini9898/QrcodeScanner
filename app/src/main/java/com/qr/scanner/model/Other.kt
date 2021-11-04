package com.qr.scanner.model

class Other(val text: String): Parsers {
    override val parser = ParsedResultType.OTHER
    override fun toFormattedText(): String = text
    override fun toBarcodeText(): String = text
}