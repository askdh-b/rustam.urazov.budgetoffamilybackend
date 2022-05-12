package rustam.urazov

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import rustam.urazov.storage.UserService

fun DI.MainBuilder.bindServices() {
    bind<UserService>() with singleton { UserService() }
}