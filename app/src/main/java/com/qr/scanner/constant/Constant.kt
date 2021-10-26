package com.qr.scanner.constant


//Email
const val MATMSG_SCHEMA_PREFIX = "MATMSG:"
const val MATMSG_EMAIL_PREFIX = "TO:"
const val MATMSG_SUBJECT_PREFIX = "SUB:"
const val MATMSG_BODY_PREFIX = "BODY:"
const val MATMSG_SEPARATOR = ";"

const val MAILTO_SCHEMA_PREFIX = "mailto:"

//Phone
const val PREFIX = "tel:"

//SMS
const val SMS_PREFIX = "smsto:"
const val SEPARATOR = ":"


//WIFI

val WIFI_REGEX = """^WIFI:((?:.+?:(?:[^\\;]|\\.)*;)+);?$""".toRegex()
val PAIR_REGEX = """(.+?):((?:[^\\;]|\\.)*);""".toRegex()
const val SCHEMA_PREFIX = "WIFI:"
const val ENCRYPTION_PREFIX = "T:"
const val NAME_PREFIX = "S:"
const val PASSWORD_PREFIX = "P:"
const val IS_HIDDEN_PREFIX = "H:"
const val ANONYMOUS_IDENTITY_PREFIX = "AI:"
const val IDENTITY_PREFIX = "I:"
const val EAP_PREFIX = "E:"
const val PHASE2_PREFIX = "PH2:"
const val WIFI_SEPARATOR = ";"

//Event
const val EVENT_SCHEMA_PREFIX = "BEGIN:VEVENT"
const val SCHEMA_SUFFIX = "END:VEVENT"
const val PARAMETERS_SEPARATOR_1 = "\n"
const val PARAMETERS_SEPARATOR_2 = "\r"
const val UID_PREFIX = "UID:"
const val STAMP_PREFIX = "DTSTAMP:"
const val ORGANIZER_PREFIX = "ORGANIZER:"
const val DESCRIPTION_PREFIX = "DESCRIPTION:"
const val LOCATION_PREFIX = "LOCATION:"
const val START_PREFIX = "DTSTART:"
const val END_PREFIX = "DTEND:"
const val SUMMARY_PREFIX = "SUMMARY:"

//Contact
private const val CONTACT_SCHEMA_PREFIX = "BEGIN:VCARD"
private const val ADDRESS_SEPARATOR = ","



const val KEY_DECODE_1D_PRODUCT = "preferences_decode_1D_product"
const val KEY_DECODE_1D_INDUSTRIAL = "preferences_decode_1D_industrial"
const val KEY_DECODE_QR = "preferences_decode_QR"
const val KEY_DECODE_DATA_MATRIX = "preferences_decode_Data_Matrix"
const val KEY_DECODE_AZTEC = "preferences_decode_Aztec"
const val KEY_DECODE_PDF417 = "preferences_decode_PDF417"

const val KEY_CUSTOM_PRODUCT_SEARCH = "preferences_custom_product_search"

const val TEXT = "text"
const val URL = "url"
const val SMS = "sms"
const val PHONE = "phone"
const val EMAIL = "email"
const val RESULT = "result"
const val TYPE = "type"
