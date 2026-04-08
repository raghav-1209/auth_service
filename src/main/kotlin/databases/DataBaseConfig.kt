package com.databases

import com.dataclasses.FcmData
import com.dataclasses.RefreshToken
import com.dataclasses.SignInData
import org.h2.engine.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

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
    fun saveRefreshToken(token: RefreshToken): Boolean {
        return transaction(database) {
            try {
                transaction(database) {
                    Tables.jwt_Token.upsert {
                        it[user_uid] = token.uid
                        it[this.token] = token.refreshToken
                    }
                }
                true
            } catch (e: Exception) {
                false
            }
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
    fun ResultRow.toRefreshToken()= RefreshToken(
        uid = this[Tables.jwt_Token.user_uid],
        refreshToken = this[Tables.jwt_Token.token]
    )
    fun getRefreshToken(token: String): RefreshToken? {
        return transaction (database) {
                Tables.jwt_Token.selectAll().where{
                    Tables.jwt_Token.token eq token
                }.singleOrNull()?.toRefreshToken()

        }
    }
}