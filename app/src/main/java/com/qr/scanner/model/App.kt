package com.qr.scanner.model

import com.qr.scanner.extension.removePrefixIgnoreCase
import com.qr.scanner.extension.startsWithAnyIgnoreCase
import com.qr.scanner.extension.unsafeLazy


class App(val url: String) : Parsers {

    companion object {
        private val PREFIXES = listOf("market://details?id=", "market://search", "http://play.google.com/", "https://play.google.com/")

        fun parse(text: String): App? {
            if (text.startsWithAnyIgnoreCase(PREFIXES).not()) {
                return null
            }
            return App(text)
        }

        fun fromPackage(packageName: String): App {
            return App(PREFIXES[0] + packageName)
        }
    }

    override val parser = ParsedResultType.APP
    override fun toFormattedText(): String = url
    override fun toBarcodeText(): String = url

    val appPackage by unsafeLazy {
        url.removePrefixIgnoreCase(PREFIXES[0])
    }
}