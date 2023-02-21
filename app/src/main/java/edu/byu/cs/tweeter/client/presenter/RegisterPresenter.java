package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {

    public interface View{

        void registerStart(String s);

        void registerSuccess(User registeredUser);

        void displayMessage(String s);
    }

    private View view;

    private UserService userService;

    public RegisterPresenter(View view){
        this.view = view;
        this.userService = new UserService();
    }

    public void registerUser(String firstName, String lastName, String aliasName, String password, Drawable icon) {
        validateRegistration(firstName, lastName, aliasName, password, icon);
        view.registerStart("Registering...");


        // Convert image to byte array.
        Bitmap image = ((BitmapDrawable) icon).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        byte[] imageBytes = bos.toByteArray();

        // Intentionally, Use the java Base64 encoder so it is compatible with M4.
        String imageBytesBase64 = Base64.getEncoder().encodeToString(imageBytes);

        // Send register request.
        userService.registerUser(firstName, lastName, aliasName, password, imageBytesBase64, new RegisterObserver());
    }

    public void validateRegistration(String firstName, String lastName, String aliasName, String password, Drawable icon) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (aliasName.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (aliasName.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (aliasName.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        if (icon == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }

    private class RegisterObserver implements UserService.RegisterObserver{

        @Override
        public void handleSuccess(User registeredUser) {
            view.registerSuccess(registeredUser);
        }

        @Override
        public void handleFailure(String s) {
            view.displayMessage("Failed to register: " + s);
        }

        @Override
        public void handleException(Exception ex) {
            view.displayMessage("Failed to register because of exception: " + ex.getMessage());
        }
    }
}
