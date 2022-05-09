package rustam.urazov.models

data class Session(val userId: Int, val token: Token)

val sessionStorage = mutableListOf<Session>()