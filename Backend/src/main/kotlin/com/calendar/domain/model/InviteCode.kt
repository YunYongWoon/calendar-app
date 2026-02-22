package com.calendar.domain.model

@JvmInline
value class InviteCode(val value: String) {
    init {
        require(INVITE_CODE_REGEX.matches(value)) { "초대 코드는 6자리 영숫자여야 합니다: $value" }
    }

    companion object {
        private val INVITE_CODE_REGEX = Regex("^[A-Za-z0-9]{6}$")
        private val CHARS = ('A'..'Z') + ('0'..'9')

        fun generate(): InviteCode {
            val code = (1..6).map { CHARS.random() }.joinToString("")
            return InviteCode(code)
        }
    }
}
