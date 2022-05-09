package rustam.urazov

import rustam.urazov.plugins.AuthSession
import rustam.urazov.plugins.sessionStorage
import java.security.MessageDigest
import kotlin.random.Random

fun md5(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(Charsets.UTF_8))

fun ByteArray.toHex() = joinToString(separator = "") { byte -> "%02x".format(byte) }

fun generateToken(): String {
    val random = Random.nextInt(10000, 9999999)
    val str = random.toString()
    return md5(str).toHex()
}

fun isAuthorized(authSession: AuthSession?): Boolean =
    if (authSession != null)
            (authSession.token) == (sessionStorage.find { it.userId == authSession.userId }?.token)
    else false