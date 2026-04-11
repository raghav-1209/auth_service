package com.databases

import com.dataclasses.FcmData
import com.dataclasses.RefreshToken
import com.dataclasses.RefreshTokenEntity
import com.dataclasses.SignInData
import org.h2.engine.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert
import java.security.MessageDigest

class DataBaseConfig (val database: Database) {
    fun saveUser(user: SignInData): Boolean {
        return transaction(database) {
            try {
                Tables.users.insert {
                    it[Tables.users.uid] = user.uid
                    it[Tables.users.email] = user.email
                    it[Tables.users.name] = user.name
                }

                true
            } catch (e: Exception) {
                false
            }

        }
        }


    fun ResultRow.toUser() = SignInData(
        email = this[Tables.users.email],
        name = this[Tables.users.name],
        uid = this[Tables.users.uid]

    )
    fun getEmail(email: String): SignInData? {
        return transaction(database) {
            Tables.users
                .selectAll()
                .where { Tables.users.email eq email }
                .singleOrNull()
                ?.toUser()
        }
    }

    fun saveFcmToken(token: FcmData): Boolean {
        return transaction(database) {
            try {
                Tables.fcmTokens.upsert {
                    it[user_uid] = token.uid
                    it[this.token] = token.token
                }
                true

            }catch (e: Exception){
                false
            }

        }
    }
    fun saveRefreshToken(tokenData: RefreshToken): Boolean {
        return transaction(database) {
            try {
                Tables.RefreshTokens.insert {
                    it[userUid] = tokenData.uid
                    it[tokenHash] = tokenData.hashToken
                    it[expiresAt] = tokenData.expiresAt
                    it[createdAt] = System.currentTimeMillis()
                }
                true
            } catch (e: Exception) {
                false
            }
        }
    }
    fun  ResultRow.toEntity(): RefreshTokenEntity {
        return RefreshTokenEntity(
            hashToken = this[Tables.RefreshTokens.tokenHash],
            expiresAt = this[Tables.RefreshTokens.expiresAt],
            uid = this[Tables.RefreshTokens. userUid],
            createdAt = this[Tables.RefreshTokens.createdAt],
            id = this[Tables.RefreshTokens.id]


        )
    }
    fun getRefreshToken(hash: String): RefreshTokenEntity? {
        return transaction(database) {
            Tables.RefreshTokens
                .selectAll()
                .where { Tables.RefreshTokens.tokenHash eq hash }
                .singleOrNull()
                ?.toEntity()
        }
    }



}
fun hash(token: String): String {
    return MessageDigest
        .getInstance("SHA-256")
        .digest(token.toByteArray())
        .joinToString("") { "%02x".format(it) }
}