package dev.redcom1988.hermes.ui.screen.link_account

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import dev.redcom1988.hermes.core.util.extension.inject
import dev.redcom1988.hermes.domain.auth.model.AuthResult
import dev.redcom1988.hermes.domain.auth.AuthRepository
import dev.redcom1988.hermes.ui.util.ToastHelper
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LinkAccountScreenModel(
    private val authRepository: AuthRepository = inject(),
    private val toastHelper: ToastHelper = inject(),
): StateScreenModel<LinkAccountScreenState>(LinkAccountScreenState()) {

    fun linkAccount(method: LinkAccountMethod) {
        screenModelScope.launch {
            if (authRepository.getCurrentUser() == null) {
                if (!signInAnonymously()) return@launch
            }
            handleLink(method)
        }
    }

    fun unlinkAccount(method: LinkAccountMethod) {
        screenModelScope.launch {
            if (authRepository.getCurrentUser() == null) {
                if (!signInAnonymously()) return@launch
            }
            handleUnlink(method)
        }
    }

    private suspend fun signInAnonymously(): Boolean {
        val anonymousSignInResult = authRepository.signIn(
            AuthRepository.createAnonymousSignInOptions()
        )
        when(anonymousSignInResult) {
            is AuthResult.Success -> return true
            is AuthResult.Error -> {
                toastHelper.show(anonymousSignInResult.exception.message ?: "Unknown Error")
                return false
            }
            is AuthResult.Cancelled -> {
                return false
            }
        }
    }

    private suspend fun handleLink(method: LinkAccountMethod) {
        val result = authRepository.linkAccount(
            when(method) {
                is LinkAccountMethod.Basic -> {
                    mutableState.update {
                        it.copy(
                            basicLinkState = it.basicLinkState.copy(loading = true)
                        )
                    }
                    AuthRepository.createBasicSignInOptions(
                        method.email, method.password
                    )
                }
                is LinkAccountMethod.Google -> {
                    mutableState.update {
                        it.copy(
                            googleLinkState = it.googleLinkState.copy(loading = true)
                        )
                    }
                    AuthRepository.createGoogleSignInOptions()
                }
            }
        )
        when(result) {
            is AuthResult.Error -> {
                when(method) {
                    is LinkAccountMethod.Basic -> mutableState.update {
                        it.copy(basicLinkState = it.basicLinkState.copy(loading = false))
                    }
                    is LinkAccountMethod.Google -> mutableState.update {
                        it.copy(basicLinkState = it.googleLinkState.copy(loading = false))
                    }
                }
                toastHelper.show(result.exception.message ?: "Unknown Error")
            }
            is AuthResult.Success -> {
                when(method) {
                    is LinkAccountMethod.Basic -> mutableState.update {
                        it.copy(
                            basicLinkState = it.basicLinkState.copy(
                                linked = true,
                                loading = false,
                            )
                        )
                    }
                    is LinkAccountMethod.Google -> mutableState.update {
                        it.copy(
                            basicLinkState = it.googleLinkState.copy(
                                linked = true,
                                loading = false,
                            )
                        )
                    }
                }
            }
            is AuthResult.Cancelled -> {
                mutableState.update {
                    it.copy(
                        basicLinkState = it.basicLinkState.copy(loading = false),
                        googleLinkState = it.googleLinkState.copy(loading = false),
                    )
                }
            }
        }
    }

    private suspend fun handleUnlink(method: LinkAccountMethod) {
        val result = authRepository.unlinkAccount(
            when(method) {
                is LinkAccountMethod.Basic -> {
                    mutableState.update {
                        it.copy(
                            basicLinkState = it.basicLinkState.copy(loading = true)
                        )
                    }
                    AuthRepository.createBasicSignInOptions(
                        method.email, method.password
                    )
                }
                is LinkAccountMethod.Google -> {
                    mutableState.update {
                        it.copy(
                            googleLinkState = it.googleLinkState.copy(loading = true)
                        )
                    }
                    AuthRepository.createGoogleSignInOptions()
                }
            }
        )
        when(method) {
            is LinkAccountMethod.Basic -> {
                mutableState.update {
                    it.copy(
                        basicLinkState = it.basicLinkState.copy(
                            linked = !result,
                            loading = false,
                        )
                    )
                }
            }
            is LinkAccountMethod.Google -> {
                mutableState.update {
                    it.copy(
                        googleLinkState = it.googleLinkState.copy(
                            linked = !result,
                            loading = false,
                        )
                    )
                }
            }
        }
    }

}