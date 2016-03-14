package de.otto.hmac.authentication;

public class AuthenticationService {

    private final UserRepository userRepository;

    public AuthenticationService(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthenticationResult validate(WrappedRequest request) {
        String sentSignature = RequestSigningUtil.getSignature(request);

        String[] split = sentSignature.split(":");
        String username = split[0];

        String secretKey = userRepository.getKey(username);
        if (secretKey == null) {
            return AuthenticationResult.fail();
        }
        if (RequestSigningUtil.checkRequest(request, secretKey)) {
            return AuthenticationResult.success(username);
        }
        return AuthenticationResult.fail();
    }
}
