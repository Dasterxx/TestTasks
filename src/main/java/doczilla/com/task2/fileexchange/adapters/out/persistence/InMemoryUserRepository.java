package doczilla.com.task2.fileexchange.adapters.out.persistence;

import doczilla.com.task2.fileexchange.domain.model.AccessToken;
import doczilla.com.task2.fileexchange.domain.model.UserId;
import doczilla.com.task2.fileexchange.domain.repository.UserRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserRepository implements UserRepository {

    private final Map<String, UserId> tokens = new ConcurrentHashMap<>();
    private final Map<String, UserId> users = new ConcurrentHashMap<>();

    @Override
    public Optional<UserId> findByToken(AccessToken token) {
        return Optional.ofNullable(tokens.get(token.value()));
    }

    @Override
    public AccessToken createToken(UserId userId) {
        AccessToken token = AccessToken.generate();
        tokens.put(token.value(), userId);
        return token;
    }

    @Override
    public UserId createUser(String username) {
        UserId userId = UserId.generate();
        users.put(username, userId);
        return userId;
    }
}
