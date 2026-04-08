package com

import com.databases.DataBaseConfig
import com.dataclasses.FcmData
import com.dataclasses.Info
import com.dataclasses.LoginData
import com.dataclasses.RefreshToken
import com.dataclasses.SignInData
import com.dataclasses.UserSession
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.example.com.raghav.jwt.JwtService
import org.h2.engine.User
import java.util.*


fun Application.configureRouting(dataBaseConfig: DataBaseConfig) {


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        configAuth(dataBaseConfig)

    }
}
fun Routing.configAuth(dataBaseConfig: DataBaseConfig) {
    route("/auth"){
        post("/signIn") {
            try {
                val data = call.receive<SignInData>() ?: return@post
                println(data.email)
                val response=dataBaseConfig.saveUser(data)
                if(response){
                    println("Successfully saved in!")
                }else{
                    println("Error saving in!")
                }

                val randomToken = UUID.randomUUID().toString()
                val accessToken = JwtService.generateToken(data.uid)
                val isSaved= dataBaseConfig.saveRefreshToken(RefreshToken(randomToken,data.uid))
                if(!isSaved) {
                    println("cannot Insert in db a refreshttoken")
                }
                println("The server sends jwttoen to client ${accessToken}")
                call.respond(UserSession(randomToken, accessToken))
            }catch (e: Exception) {
                println(e.localizedMessage)
            }
        }
        post("/login"){
            val data=call.receive<LoginData>()
            val userInfo=dataBaseConfig.getEmail(data.email)
            if(userInfo==null){
                call.respond(status = HttpStatusCode.BadRequest, message = "Email is required")
                return@post
            }
            val randomToken = UUID.randomUUID().toString()
            val accessToken = JwtService.generateToken(userInfo.uid)
            val isSaved= dataBaseConfig.saveRefreshToken(RefreshToken(randomToken,userInfo.uid))
            if(isSaved) {
                println("cannot Insert in db a refreshttoken")
            }
            println("The server sends jwttoen to client ${accessToken}")
            call.respond(UserSession(randomToken, accessToken))


        }
        post("/refreshToken") {
            println("The RefrshFun Called")
            val data=call.receive<Info>()?:return@post
            val isExit=dataBaseConfig.getRefreshToken(data.token)
            if(isExit==null){
                call.respond(HttpStatusCode.Conflict, message = "Unauthorized USer")
                return@post
            }
            val refreshToken= UUID.randomUUID().toString()
            val accessToken= JwtService.generateToken(isExit.uid)
            call.respond(UserSession(refreshToken, accessToken))

        }


        }
    }





