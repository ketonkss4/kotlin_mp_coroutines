package co.happybits.mpcompanion.authentication.dependencies

import co.happybits.mpcompanion.authentication.AuthActivity
import co.happybits.mpcompanion.authentication.AuthViewModel
import co.happybits.mpcompanion.injection.ConcurrencyModule
import co.happybits.mpcompanion.networking.ServiceClientHelper
import dagger.Component

@AuthScope @Component(modules = [AuthModule::class, ConcurrencyModule::class])
interface AuthComponent {
    val authViewModelViewModel: AuthViewModel
    fun inject(authActivity: AuthActivity)
    fun inject(authActivity: ServiceClientHelper)
}