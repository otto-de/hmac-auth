package de.otto.hmac.authentication;


import org.joda.time.Instant;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;

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

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");

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
    public void shouldEncryptRequestInfo() throws NoSuchAlgorithmException, IOException {

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", formattedDateOfNow());
        request.setContent("{ \"key\": \"value\"}".getBytes());

        String encrypted = RequestSigningUtil.createRequestSignature(wrap(request), "secretKey");
        String encrypted2 = RequestSigningUtil.createRequestSignature(wrap(request), "secretKey");

        System.out.println(encrypted);

        assertThat(encrypted, is(encrypted2));

        assertThat(encrypted, not(startsWith("PUT\n2012.12.24-00:00:00\nsome/URI\n")));
    }

    @Test
    public void shouldAcceptCorrectlySignedRequestIfRequestTimeStampIsValid() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", formattedDateOfNow());
        request.setContent("{ \"key\": \"value\"}".getBytes());

        String requestSignatur = RequestSigningUtil.createRequestSignature(wrap(request), "secretKey");
        request.addHeader("x-hmac-auth-signature", "username:" + requestSignatur);

        boolean valid = RequestSigningUtil.checkRequest(wrap(request), "secretKey");
        assertThat(valid, is(true));
    }

    @Test
    public void shouldRejectCorrectlySignedRequestIfRequestTimeStampIsTooMuchInTheFuture() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        Instant timeStampToMuchInFuture = new Instant().plus(500000);
        request.addHeader("x-p13n-date", timeStampToMuchInFuture);
        request.setContent("{ \"key\": \"value\"}".getBytes());

        String requestSignatur = RequestSigningUtil.createRequestSignature(wrap(request), "secretKey");
        request.addHeader("x-hmac-auth-signature", "username:" + requestSignatur);

        boolean valid = RequestSigningUtil.hasValidRequestTimeStamp(wrap(request));
        assertThat(valid, is(false));
    }

    @Test
    public void shouldRejectCorrectlySignedRequestIfRequestTimeStampIsExpired() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");

        Instant timeStampExpired = new Instant().minus(500000L);
        request.addHeader("x-p13n-date", timeStampExpired.toString());
        request.setContent("{ \"key\": \"value\"}".getBytes());

        String requestSignatur = RequestSigningUtil.createRequestSignature(wrap(request), "secretKey");
        request.addHeader("x-hmac-auth-signature", "username:" + requestSignatur);


        boolean valid = RequestSigningUtil.hasValidRequestTimeStamp(wrap(request));
        assertThat(valid, is(false));
    }

    @Test
    public void shouldRejectIncorrectlySignedRequest() throws NoSuchAlgorithmException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "some/URI");
        request.addHeader("x-hmac-auth-date", formattedDateOfNow());
        request.addHeader("x-hmac-auth-signature", "username:FalscheSignatur=");
        request.setContent("{ \"key\": \"value\"}".getBytes());

        boolean valid = RequestSigningUtil.checkRequest(wrap(request), "secretKey");
        assertThat(valid, is(false));
    }

    private static String formattedDateOfNow() {
        return new Instant().toString();
    }


}
