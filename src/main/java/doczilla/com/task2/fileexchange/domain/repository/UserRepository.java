package doczilla.com.task2.fileexchange.domain.repository;

import doczilla.com.task2.fileexchange.domain.model.AccessToken;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.util.Optional;

public interface UserRepository {
    Optional<UserId> findByToken(AccessToken token);
    AccessToken createToken(UserId userId);
    UserId createUser(String username);
}