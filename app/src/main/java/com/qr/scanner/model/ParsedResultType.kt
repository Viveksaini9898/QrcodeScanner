package com.qr.scanner.model

enum class ParsedResultType {
    APP,
    BOOKMARK,
    EMAIL,
    GEO,
    GOOGLE_MAPS,
    MMS,
    MECARD,
    PHONE,
    SMS,
    URL,
    VEVENT,
    VCARD,
    WIFI,
    YOUTUBE,
    OTHER;
}

interface Parsers {
    val parser: ParsedResultType
    fun toFormattedText(): String
    fun toBarcodeText(): String
}
