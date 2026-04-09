package com

import com.databases.DataBaseConfig
import com.databases.hash
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
import java.security.SecureRandom
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
                val response = dataBaseConfig.saveUser(data)
                if (response) {
                    println("Successfully saved in!")
                } else {
                    println("Error saving in!")
                }
                val session = generateSession(data.uid, dataBaseConfig)

                if (session == null) {
                    call.respond(HttpStatusCode.InternalServerError, "Session creation failed")
                    return@post
                }

                call.respond(HttpStatusCode.OK, session)
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
            val session = generateSession(userInfo.uid, dataBaseConfig)

            if (session == null) {
                call.respond(HttpStatusCode.InternalServerError, "Session creation failed")
                return@post
            }

            call.respond(HttpStatusCode.OK, session)
        }
        post("/refreshToken") {
            val data = call.receive<Info>()

            val hashed = hash(data.token)
            val tokenRow = dataBaseConfig.getRefreshToken(hashed)

            if (tokenRow == null) {
                call.respond(HttpStatusCode.Unauthorized)
                return@post
            }

            if (tokenRow.expiresAt < System.currentTimeMillis()) {
                call.respond(HttpStatusCode.Unauthorized, "Expired")
                return@post
            }

            if (tokenRow.revoked) {
                call.respond(HttpStatusCode.Unauthorized, "Revoked")
                return@post
            }

            //  CREATE NEW TOKEN
            val rawToken = generateSecureToken()
            val newHash = hash(rawToken)
            val expiresAt = System.currentTimeMillis() + Constants.refeshTokenExpiry

            //  ROTATE (THIS IS THE KEY)
            dataBaseConfig.rotateToken(
                oldId = tokenRow.id,
                newToken = RefreshToken(newHash, tokenRow.uid, expiresAt)
            )

            val accessToken = JwtService.generateToken(tokenRow.uid)

            call.respond(
                UserSession(
                    refreshToken = rawToken,
                    token = accessToken
                )
            )
        }



        post("/fcmToken") {
            println("the fcm fun is called")
            val data=call.receive<FcmData>() ?: return@post
            val response=dataBaseConfig.saveFcmToken(data)
            if(response){
                println("Successfully saved in!")
                call.respond(HttpStatusCode.OK)
            }else{
                println("Error saving in!")
                call.respond(HttpStatusCode.NotImplemented)
            }
        }


        }
    }


fun generateSecureToken(): String {
    val bytes = ByteArray(32)
    SecureRandom().nextBytes(bytes)
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}

fun generateSession(
    uid: String,
    dataBaseConfig: DataBaseConfig
): UserSession? {

    val rawToken = generateSecureToken()
    val tokenHash = hash(rawToken)
    val expiresAt = System.currentTimeMillis() + Constants.refeshTokenExpiry

    val saved = dataBaseConfig.saveRefreshToken(
        RefreshToken(tokenHash, uid, expiresAt)
    )

    if (!saved) {
        println("Failed to save refresh token")
        return null
    }

    val accessToken = JwtService.generateToken(uid)

    return UserSession(
        refreshToken = rawToken,
        accessToken
    )
}
