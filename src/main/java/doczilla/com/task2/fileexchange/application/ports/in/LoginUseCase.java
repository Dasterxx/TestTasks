package doczilla.com.task2.fileexchange.application.ports.in;

public interface LoginUseCase {
    LoginResult login(String username, String password);

    record LoginResult(String token, String userId) {}
}
