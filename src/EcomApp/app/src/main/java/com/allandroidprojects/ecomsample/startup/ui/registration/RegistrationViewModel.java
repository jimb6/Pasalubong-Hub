package com.allandroidprojects.ecomsample.startup.ui.registration;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.allandroidprojects.ecomsample.R;
import com.allandroidprojects.ecomsample.startup.data.RegistrationRepository;
import com.allandroidprojects.ecomsample.startup.data.model.LoggedInUser;

public class RegistrationViewModel extends ViewModel {

    private MutableLiveData<RegistrationFromState> registrationFormState = new MutableLiveData<>();
    private RegistrationRepository registrationRepository;
    LiveData<LoggedInUser> accountRegistrationLiveData;

    RegistrationViewModel(RegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    LiveData<RegistrationFromState> getRegistrationFormState() {
        return registrationFormState;
    }

    public void register(String email, String password){
        accountRegistrationLiveData = registrationRepository.firebaseRegistrationWithEmailAndPassword(email, password);
    }

    public void registrationDataChanged(String username, String password, String confirmpassword) {
        if (!isUserNameValid(username)) {
            registrationFormState.setValue(new RegistrationFromState(R.string.invalid_username, null, null));
        } else if (!isPasswordValid(password)) {
            registrationFormState.setValue(new RegistrationFromState(null, R.string.invalid_password, null));
        } else if (!isConfirmPasswordValid(password, confirmpassword)){
            registrationFormState.setValue(new RegistrationFromState(null, null, R.string.invalid_confirm_password));
        }else {
            registrationFormState.setValue(new RegistrationFromState(true));
        }
    }

    public LiveData<LoggedInUser> registrationResult(){
        return accountRegistrationLiveData;
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    // A placeholder password validation check
    private boolean isConfirmPasswordValid(String password, String confirmPassword) {
        return password != null && password.trim().length() > 5 && password.equals(confirmPassword);
    }
}
