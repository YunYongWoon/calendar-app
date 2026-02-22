package com.calendar.domain.model

import java.security.SecureRandom

@JvmInline
value class InviteCode(val value: String) {
    init {
        require(INVITE_CODE_REGEX.matches(value)) { "초대 코드는 6자리 대문자 영숫자여야 합니다: $value" }
    }

    companion object {
        private val INVITE_CODE_REGEX = Regex("^[A-Z0-9]{6}$")
        private val CHARS = ('A'..'Z') + ('0'..'9')
        private val SECURE_RANDOM = SecureRandom()

        fun generate(): InviteCode {
            val code = (1..6).map { CHARS[SECURE_RANDOM.nextInt(CHARS.size)] }.joinToString("")
            return InviteCode(code)
        }

        fun of(value: String): InviteCode = InviteCode(value.uppercase())
    }
}
