package com.calendar.interfaces.rest.controller

import com.calendar.infrastructure.config.SecurityConfig
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(HealthController::class)
@Import(SecurityConfig::class)
@ActiveProfiles("test")
class HealthControllerTest(
    private val mockMvc: MockMvc,
) : DescribeSpec({

    extensions(SpringExtension)

    describe("GET /health") {
        context("요청을 보내면") {
            it("200 OK와 status=UP을 반환한다") {
                mockMvc.get("/health")
                    .andExpect {
                        status { isOk() }
                        jsonPath("$.status") { value("UP") }
                        jsonPath("$.timestamp") { exists() }
                        jsonPath("$.version") { exists() }
                    }
            }
        }
    }

    describe("GET /health/detail") {
        context("요청을 보내면") {
            it("200 OK와 javaVersion을 포함한 상세 정보를 반환한다") {
                mockMvc.get("/health/detail")
                    .andExpect {
                        status { isOk() }
                        jsonPath("$.status") { value("UP") }
                        jsonPath("$.timestamp") { exists() }
                        jsonPath("$.version") { exists() }
                        jsonPath("$.javaVersion") { exists() }
                    }
            }
        }
    }
})
