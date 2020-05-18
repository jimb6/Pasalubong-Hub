package com.allandroidprojects.ecomsample.data;

import com.allandroidprojects.ecomsample.data.models.LoggedInUser;
import com.allandroidprojects.ecomsample.data.models.Result;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public Result<LoggedInUser> login(String username, String password) {

        try {
            // TODO: handle loggedInUser authentication

            LoggedInUser user = new LoggedInUser(
                    java.util.UUID.randomUUID().toString(),
                    "Jane Doe", "jane.doe@gmail.com","https://static.pexels.com/photos/5854/sea-woman-legs-water-medium.jpg");
            return new Result.Success<>(user);
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: revoke authentication
    }
}
