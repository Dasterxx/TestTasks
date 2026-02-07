package doczilla.com.task2.fileexchange.adapters.in.web;

import com.sun.net.httpserver.HttpExchange;
import doczilla.com.task2.fileexchange.domain.model.AccessToken;
import doczilla.com.task2.fileexchange.domain.model.UserId;

import java.util.Optional;

public class AuthExtractor {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    public Optional<UserId> extract(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return Optional.empty();
        }

        String tokenValue = header.substring(7).trim();

        try {
            return Optional.of(UserId.of(tokenValue));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<AccessToken> extractToken(HttpExchange exchange) {
        String header = exchange.getRequestHeaders().getFirst(AUTH_HEADER);
        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            return Optional.empty();
        }
        return Optional.of(AccessToken.of(header.substring(BEARER_PREFIX.length())));
    }
}
