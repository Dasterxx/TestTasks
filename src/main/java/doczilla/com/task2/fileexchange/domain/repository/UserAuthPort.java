package doczilla.com.task2.fileexchange.domain.repository;

import doczilla.com.task2.fileexchange.domain.model.AccessToken;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.util.Optional;

public interface UserAuthPort {

    Optional<UserId> authenticate(AccessToken token);

    AccessToken generateToken(UserId userId);

    void revokeToken(AccessToken token);

    boolean isValid(AccessToken token);
}
