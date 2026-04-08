package com

import com.databases.DataBaseConfig
import com.databases.DataBaseFactory
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
    val dataBaseFac= DataBaseFactory()
    dataBaseFac.init()
    val database=dataBaseFac.database
    val dataBaseConfig= DataBaseConfig(database)
    configureRouting(dataBaseConfig)
}
