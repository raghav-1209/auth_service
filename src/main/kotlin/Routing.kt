package com

import com.dataclasses.JwtConfig
import com.dataclasses.SignInData
import com.dataclasses.UserSession
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

fun Application.configureRouting(jwtConfig: JwtConfig) {


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        configAuth(jwtConfig =jwtConfig )

    }
}
fun Routing.configAuth(jwtConfig: JwtConfig) {
    route("/auth"){
        post("/signIn") {
            val data=call.receive<SignInData>()
            println(data.email)
            val randomToken= UUID.randomUUID().toString()
            val accessToken=generateToken(data.uid,jwtConfig)
            println("The server sends jwttoen to client ${accessToken}")
            call.respond(UserSession(randomToken, accessToken))
        }
    }



    }

