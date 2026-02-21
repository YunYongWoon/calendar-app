package com.calendar.interfaces.rest.controller

import com.calendar.interfaces.rest.dto.HealthDetailResponse
import com.calendar.interfaces.rest.dto.HealthResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@Tag(name = "Health", description = "서버 상태 확인 API")
@RestController
@RequestMapping("/health")
class HealthController {

    companion object {
        private const val APP_VERSION = "0.0.1-SNAPSHOT"
    }

    @Operation(summary = "기본 헬스체크", description = "서버 상태, 타임스탬프, 버전 정보를 반환합니다.")
    @GetMapping
    fun health(): HealthResponse = HealthResponse(
        status = "UP",
        timestamp = Instant.now(),
        version = APP_VERSION,
    )

    @Operation(summary = "상세 헬스체크", description = "기본 정보에 JVM 버전을 추가로 반환합니다.")
    @GetMapping("/detail")
    fun healthDetail(): HealthDetailResponse = HealthDetailResponse(
        status = "UP",
        timestamp = Instant.now(),
        version = APP_VERSION,
        javaVersion = System.getProperty("java.version"),
    )
}
