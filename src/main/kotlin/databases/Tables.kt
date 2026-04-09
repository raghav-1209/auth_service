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
        val user_uid=reference("user_id", Tables.users.uid)
        val token=text("fcm_token").uniqueIndex()
    }
    object RefreshTokens : Table("auth.refresh_tokens") {
        val id = integer("id").autoIncrement()

        val userUid = reference("user_id", Tables.users.uid)

        val tokenHash = text("token_hash")

        val expiresAt = long("expires_at")

        val createdAt = long("created_at")

        val revoked = bool("revoked").default(false)


        override val primaryKey = PrimaryKey(id)
    }
}