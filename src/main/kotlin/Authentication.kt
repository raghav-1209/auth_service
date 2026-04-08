package com

import com.dataclasses.JwtConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.response.respond
import org.example.com.raghav.jwt.JwtVerifier
fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("jwt_auth"){
            verifier(JwtVerifier.verifier)
            validate { credential ->
                val uid = credential.payload.getClaim("userId").asString()

                if (!uid.isNullOrBlank()) {
                    JWTPrincipal(credential.payload)
                } else null
            }

            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, "Invalid Token")
            }

            }

    }

    }
