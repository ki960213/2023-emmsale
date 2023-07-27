package com.emmsale.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.emmsale.data.common.ApiError
import com.emmsale.data.common.ApiException
import com.emmsale.data.common.ApiSuccess
import com.emmsale.data.login.Login
import com.emmsale.data.login.LoginRepository
import com.emmsale.data.token.Token
import com.emmsale.data.token.TokenRepository
import com.emmsale.presentation.base.viewmodel.BaseViewModel
import com.emmsale.presentation.base.viewmodel.DispatcherProvider
import com.emmsale.presentation.ui.login.uistate.LoginUiState
import kotlinx.coroutines.launch

class LoginViewModel(
    dispatcherProvider: DispatcherProvider,
    private val loginRepository: LoginRepository,
    private val tokenRepository: TokenRepository,
) : BaseViewModel(dispatcherProvider) {
    private val _loginState: MutableLiveData<LoginUiState> = MutableLiveData()
    val loginState: LiveData<LoginUiState> = _loginState

    fun saveGithubCode(code: String) {
        changeLoginState(LoginUiState.Loading)
        viewModelScope.launch {
            when (val loginResult = loginRepository.saveGithubCode(code)) {
                is ApiSuccess -> handleLoginResult(loginResult.data)
                is ApiError -> changeLoginState(LoginUiState.Error)
                is ApiException -> changeLoginState(LoginUiState.Error)
            }
        }
    }

    private suspend fun handleLoginResult(loginResult: Login) {
        tokenRepository.saveToken(Token.from(loginResult))
        when (loginResult.isNewMember) {
            true -> changeLoginState(LoginUiState.Login)
            false -> changeLoginState(LoginUiState.Register)
        }
    }

    private fun changeLoginState(loginState: LoginUiState) {
        _loginState.postValue(loginState)
    }
}
