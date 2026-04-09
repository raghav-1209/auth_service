package com.databases

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class DataBaseFactory {
    lateinit var database: Database
    fun init() {
        database= Database.connect(
            url = "jdbc:postgresql://ep-noisy-forest-amq2wyx6-pooler.c-5.us-east-1.aws.neon.tech/neondb?sslmode=require&channel_binding=require?currentSchema=auth",
            driver = "org.postgresql.Driver",
            user = "neondb_owner",
            password = "npg_AWEm0fKjz6ri"
        )
        transaction(database) {
            SchemaUtils.create(
                Tables.users,
                Tables.RefreshTokens,
                Tables.fcmTokens
            )

        }

    }
}