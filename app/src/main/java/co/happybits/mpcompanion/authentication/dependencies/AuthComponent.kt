package co.happybits.mpcompanion.authentication.dependencies

import co.happybits.mpcompanion.authentication.AuthActivity
import co.happybits.mpcompanion.authentication.AuthActivityViewModel
import co.happybits.mpcompanion.injection.ConcurrencyModule
import dagger.Component

@AuthScope @Component(modules = [AuthModule::class, ConcurrencyModule::class])
interface AuthComponent {
    val authActivityViewModel: AuthActivityViewModel
    fun inject(authActivity: AuthActivity)
}