package de.otto.hmac.authentication;

import java.time.Clock;

public class AuthenticationService {

    private final UserRepository userRepository;
    private final Clock clock;

    public AuthenticationService(final UserRepository userRepository) {
        this(userRepository, Clock.systemUTC());
    }

    public AuthenticationService(final UserRepository userRepository, final Clock clock) {
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public AuthenticationResult validate(WrappedRequest request) {
        String sentSignature = RequestSigningUtil.getSignature(request);

        String[] split = sentSignature.split(":");
        String username = split[0];

        String secretKey = userRepository.getKey(username);
        if (secretKey == null) {
            return AuthenticationResult.fail();
        }
        if (RequestSigningUtil.checkRequest(request, secretKey, clock)) {
            return AuthenticationResult.success(username);
        }
        return AuthenticationResult.fail();
    }
}
