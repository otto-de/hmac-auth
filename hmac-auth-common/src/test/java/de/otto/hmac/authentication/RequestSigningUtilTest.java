package de.otto.hmac.authentication;

import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static de.otto.hmac.authentication.WrappedRequest.wrap;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

@Test
public class RequestSigningUtilTest {

    /*
    <METHOD>\n
    <DATUM>\n
    <REQUEST-URI>\n
    MD5(<BODY>)
    */

    @Test
    public void shouldBeginSignatureBaseWithHttpMethod() throws IOException {
        HttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");

        String signatureBase = RequestSigningUtil.createSignatureBase(wrap(request));

        assertThat(signatureBase, startsWith("PUT"));
    }

    @Test
    public void shouldAddDateToSignatureBase() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        String nowAsString = formattedDateOfNow();
        request.addHeader("x-hmac-auth-date", nowAsString);

        String signatureBase = RequestSigningUtil.createSignatureBase(wrap(request));

        assertThat(signatureBase, startsWith("PUT\n" + nowAsString));
    }

    @Test
    public void shouldHandleEncodedUrlsWhenCalculatingSignature() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URIäüö");
        String nowAsString = formattedDateOfNow();
        request.addHeader("x-hmac-auth-date", nowAsString);

        String signatureBaseUnencoded = RequestSigningUtil.createSignatureBase(wrap(request));

        request = new MockHttpServletRequest("PUT", "some/URI%C3%A4%C3%BC%C3%B6");
        request.addHeader("x-hmac-auth-date", nowAsString);

        String signatureBaseUrlEncoded = RequestSigningUtil.createSignatureBase(wrap(request));

        assertThat(signatureBaseUnencoded, is(signatureBaseUrlEncoded));
    }

    @Test
    public void shouldAddRequestUriToSignatureBase() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        String nowAsString = formattedDateOfNow();
        request.addHeader("x-hmac-auth-date", nowAsString);

        String signatureBase = RequestSigningUtil.createSignatureBase(wrap(request));

        assertThat(signatureBase, startsWith("PUT\n" + nowAsString + "\nsome/URI"));
    }

    @Test
    public void shouldAddBodyAsMd5ToSignatureBase() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        String nowAsString = formattedDateOfNow();
        request.addHeader("x-hmac-auth-date", nowAsString);
        String body = "{ \"key\": \"value\"}";
        request.setContent(body.getBytes());

        String signatureBase = RequestSigningUtil.createSignatureBase(wrap(request));


        assertThat(signatureBase, startsWith("PUT\n" + nowAsString + "\nsome/URI\nb4cfb98da7379e9f280c3b3d8686005d"));
    }

    @Test
    public void shouldEncryptRequestInfo() throws IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", formattedDateOfNow());
        request.setContent("{ \"key\": \"value\"}".getBytes());

        WrappedRequest wrappedRequest = wrap(request);
        String encrypted = RequestSigningUtil.createRequestSignature(wrappedRequest, "secretKey");
        String encrypted2 = RequestSigningUtil.createRequestSignature(wrappedRequest, "secretKey");

        assertThat(encrypted, is(encrypted2));

        assertThat(encrypted, not(startsWith("PUT\n2012.12.24-00:00:00\nsome/URI\n")));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenSecretKeyIsNull() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", formattedDateOfNow());
        request.setContent("{ \"key\": \"value\"}".getBytes());

        RequestSigningUtil.createRequestSignature(wrap(request), null);
    }

    @Test
    public void shouldAcceptCorrectlySignedRequestIfRequestTimeStampIsValid() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", formattedDateOfNow());
        request.setContent("{ \"key\": \"value\"}".getBytes());

        WrappedRequest wrappedRequest = wrap(request);
        String requestSignature = RequestSigningUtil.createRequestSignature(wrappedRequest, "secretKey");
        request.addHeader("x-hmac-auth-signature", "username:" + requestSignature);

        boolean valid = RequestSigningUtil.checkRequest(wrappedRequest, "secretKey", Clock.systemUTC());
        assertThat(valid, is(true));
    }

    @Test
    public void shouldRejectCorrectlySignedRequestIfRequestTimeStampIsTooMuchInTheFuture() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        Instant timeStampToMuchInFuture = Instant.now().plus(Duration.ofMillis(500000L));
        request.addHeader("x-p13n-date", timeStampToMuchInFuture);
        request.setContent("{ \"key\": \"value\"}".getBytes());

        String requestSignatur = RequestSigningUtil.createRequestSignature(wrap(request), "secretKey");
        request.addHeader("x-hmac-auth-signature", "username:" + requestSignatur);

        boolean valid = RequestSigningUtil.hasValidRequestTimeStamp(wrap(request), Clock.systemUTC());
        assertThat(valid, is(false));
    }

    @Test
    public void shouldRejectCorrectlySignedRequestIfRequestTimeStampIsExpired() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");

        Instant timeStampExpired = Instant.now().minus(Duration.ofMillis(500000L));
        request.addHeader("x-p13n-date", timeStampExpired.toString());
        request.setContent("{ \"key\": \"value\"}".getBytes());

        String requestSignatur = RequestSigningUtil.createRequestSignature(wrap(request), "secretKey");
        request.addHeader("x-hmac-auth-signature", "username:" + requestSignatur);


        boolean valid = RequestSigningUtil.hasValidRequestTimeStamp(wrap(request), Clock.systemUTC());
        assertThat(valid, is(false));
    }

    @Test
    public void shouldRejectIncorrectlySignedRequest() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", formattedDateOfNow());
        request.addHeader("x-hmac-auth-signature", "username:FalscheSignatur=");
        request.setContent("{ \"key\": \"value\"}".getBytes());

        boolean valid = RequestSigningUtil.checkRequest(wrap(request), "secretKey", Clock.systemUTC());
        assertThat(valid, is(false));
    }

    @Test
    public void shouldParseDateTimeStringInISO8601FormatWithTimeZone() throws IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", "2016-11-07T16:44:49.1+01:00");
        request.setContent("{ \"key\": \"value\"}".getBytes());
        Clock fixedClock = Clock.fixed(Instant.parse("2016-11-07T15:44:49.1Z"), ZoneOffset.UTC);

        boolean validRequestTimeStamp = RequestSigningUtil.hasValidRequestTimeStamp(wrap(request), fixedClock);

        assertThat(validRequestTimeStamp, is(true));
    }

    private static String formattedDateOfNow() {
        return Instant.now().toString();
    }


}
