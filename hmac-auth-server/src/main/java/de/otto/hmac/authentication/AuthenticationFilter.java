package de.otto.hmac.authentication;

import de.otto.hmac.HmacAttributes;
import de.otto.hmac.StringUtils;
import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static de.otto.hmac.HmacAttributes.AUTHENTICATED_USERNAME;
import static de.otto.hmac.StringUtils.toMd5;
import static de.otto.hmac.authentication.AuthenticationResult.Status.FAIL;
import static de.otto.hmac.authentication.AuthenticationResult.Status.SUCCESS;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;
import static org.slf4j.LoggerFactory.getLogger;


@Component("AuthenticationFilter")
public class AuthenticationFilter implements Filter {

    private static final Logger LOG = getLogger(AuthenticationFilter.class);

    private AuthenticationService service;

    @Required
    @Resource
    public void setService(AuthenticationService service) {
        this.service = service;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!RequestSigningUtil.hasSignature(httpRequest)) {
            chain.doFilter(httpRequest, httpResponse);
        } else {
            try {
                WrappedRequest wrapped = WrappedRequest.wrap(httpRequest);
                logDebug(wrapped);
                AuthenticationResult result = service.validate(wrapped);

                if (result.getStatus() == FAIL) {
                    httpResponse.sendError(SC_UNAUTHORIZED);
                    LOG.error("Validierung der Signatur fehlgeschlagen.");
                    return;
                }

                if (result.getStatus() == SUCCESS) {
                    LOG.debug("Validierung der Signatur erfolgreich. Username: " + result.getUsername());
                    wrapped.setAttribute(AUTHENTICATED_USERNAME, result.getUsername());
                }
                chain.doFilter(wrapped, response);
            } catch (Exception e) {
                LOG.error("Unerwartete Exception beim Validieren der Signatur.", e);
                throw e;
            }

        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

    @Override
    public void destroy() {
        // Do nothing
    }

    private void logDebug(WrappedRequest request) {
        if (LOG.isDebugEnabled()) {
            StringBuilder sb = new StringBuilder("\n\n-----------------------------------\n");
            sb.append("Validiere Request.\n");
            sb.append("Signatur: ").append(RequestSigningUtil.getSignature(request)).append("\n");
            sb.append("Body: " + request.getBody()).append("\n");
            sb.append("Body-MD5: " + toMd5(request.getBody())).append("\n");
            sb.append("Timestamp: " + RequestSigningUtil.getDateFromHeader(request)).append("\n");
            sb.append("Uri: " + request.getRequestURI()).append("\n");
            sb.append("Method: " + request.getMethod()).append("\n");
            sb.append("\n").append("---------------------\n\n");

            LOG.debug(sb.toString());
        }
    }

}
