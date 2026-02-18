package com.example.bmi.presentation.profile

import androidx.lifecycle.ViewModel




class ProfileViewModel : ViewModel() {

    var formState = ProfileFormState()
        private set

    fun onWeightChange(weight: String) {
        formState = formState.copy(weight = weight)
        validate()
    }

    fun onHeightChange(height: String) {
        formState = formState.copy(height = height)
        validate()
    }

    fun onGenderChange(gender: String) {
        formState = formState.copy(gender = gender)
        validate()
    }

    private fun validate() {
        val weightError = if (formState.weight.toDoubleOrNull() == null)
            "Enter valid weight"
        else null

        val heightError = if (formState.height.toDoubleOrNull() == null)
            "Enter valid height"
        else null

        val isValid = weightError == null &&
                heightError == null &&
                formState.gender.isNotBlank()

        formState = formState.copy(
            weightError = weightError,
            heightError = heightError,
            isValid = isValid
        )
    }
}
//    fun saveProfile(onSuccess: () -> Unit) {
//
//        //val userId = authRepository.getCurrentUserId() ?: return
//
//        val user = User(
//            id = userId,
//            email = "", // optional
//            weight = formState.weight.toDouble(),
//            height = formState.height.toDouble(),
//            gender = formState.gender
//        )
//
//        viewModelScope.launch {
//            userRepository.saveUser(user)
//            onSuccess()
//        }
//    }
//}