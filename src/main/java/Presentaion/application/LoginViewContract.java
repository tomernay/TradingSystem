package Presentaion.application;

public interface LoginViewContract {
    interface View {
        void showError(String message);
        void navigateToMain();
        void navigateToRegister();
    }

    interface Presenter {
        void login(String username, String password);
    }
}