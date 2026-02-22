package com.calendar.domain.model

@JvmInline
value class ColorHex(val value: String) {
    init {
        require(COLOR_HEX_REGEX.matches(value)) { "올바른 색상 코드 형식이 아닙니다: $value (예: #FF0000)" }
    }

    companion object {
        private val COLOR_HEX_REGEX = Regex("^#[0-9A-Fa-f]{6}$")
    }
}
