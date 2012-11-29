package de.otto.hmac.authentication;


import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AuthenticationService {

    private UserRepository userRepository;

    @Resource
    @Required
    public void setUserRepository(UserRepository userRepository) {
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
