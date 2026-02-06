package doczilla.com.task2.fileexchange.application.service;

import doczilla.com.task2.fileexchange.application.ports.in.LoginUseCase;
import doczilla.com.task2.fileexchange.domain.model.AccessToken;
import doczilla.com.task2.fileexchange.domain.model.UserId;
import doczilla.com.task2.fileexchange.domain.repository.UserRepository;

import java.util.Objects;

public class LoginService implements LoginUseCase {

    private final UserRepository userRepository;

    public LoginService(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository);
    }

    @Override
    public LoginResult login(String username, String password) {
        // В реальности: проверка пароля
        // Сейчас: создаём пользователя если нет, или находим
        UserId userId = userRepository.createUser(username);
        AccessToken token = userRepository.createToken(userId);

        return new LoginResult(token.value(), userId.toString());
    }
}
