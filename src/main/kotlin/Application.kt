package com

import com.dataclasses.JwtConfig
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureAdministration()
    configureSerialization()
    configResource()
    configStaticResource()
    configStatusPages()
    val jwtSection = environment.config.config("jwt")
    val jwtConfig = JwtConfig(
        issuer = jwtSection.property("issuer").getString(),
        audience = jwtSection.property("audience").getString(),
        realm = jwtSection.property("realm").getString(),
        secret = jwtSection.property("secret").getString()
    )
    configureAuth(jwtConfig)
    configureRouting(jwtConfig)
}
