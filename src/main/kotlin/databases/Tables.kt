package com.databases

import org.jetbrains.exposed.sql.Table

object Tables {
    object users : Table("auth.users") {
        val id = integer("id").autoIncrement()
        val name = varchar("name", 255)
        val email = varchar("email", 255)
        val uid = varchar("uid", 255).uniqueIndex()
        override val primaryKey = PrimaryKey(id)
    }
    object fcmTokens : Table("auth.fcm_tokens") {
        val id = integer("id").autoIncrement()
        val user_uid=reference("user_id", Tables.users.uid).uniqueIndex()
        val token=text("fcm_token")
    }
    object jwt_Token :Table("auth.jwt_token") {
        val id = integer("id").autoIncrement()
        val user_uid=reference("user_id", Tables.users.uid).uniqueIndex()
        val token = text("refresh_token")
    }
}