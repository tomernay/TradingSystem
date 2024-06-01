package Presentaion.application;

public interface RegisterViewContract {
    interface View {
        void showError(String message);
        void navigateToLogin();
    }

    interface Presenter {
        void register(String username, String password, String confirmPassword);
    }
}