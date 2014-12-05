package de.otto.hmac.authentication;


import com.google.common.io.ByteStreams;
import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static de.otto.hmac.authentication.WrappedRequest.wrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Test
public class WrappedRequestTest {


    @Test
    public void noDoubleWrap() throws Exception {
        MockHttpServletRequest original = new MockHttpServletRequest("PUT", "/some/uri");
        String originalBody = "{ \"some\" : \"json\" }";
        original.setContent(originalBody.getBytes());

        WrappedRequest wrappedOnce = wrap(original);

        WrappedRequest wrappedTwice = wrap(wrappedOnce);
        assertThat(wrappedOnce == wrappedTwice, is(true));
    }

    @Test
    public void shouldNotChangeRequestBody() throws Exception {
        MockHttpServletRequest original = new MockHttpServletRequest("PUT", "/some/uri");
        String originalBody = "{ \"some\" : \"json\" }";
        original.setContent(originalBody.getBytes());

        WrappedRequest faelschung = wrap(original);
        assertThat(extractContent(faelschung), is(originalBody.getBytes()));
    }


    @Test
    public void shouldNotChangeRequestBodyWithUmlauts() throws Exception {
        MockHttpServletRequest original = new MockHttpServletRequest("PUT", "/some/uri");
        String originalBody = "{ \"some\" : \"jsön\" }";
        original.setContent(originalBody.getBytes());

        WrappedRequest faelschung = wrap(original);

        assertThat(extractContent(faelschung), is(originalBody.getBytes()));
    }

    @Test
    public void shouldHaveEmptyBodyWhenEmpty() throws Exception {
        MockHttpServletRequest original = new MockHttpServletRequest("PUT", "/some/uri");

        WrappedRequest faelschung = wrap(original);

        assertThat(extractContent(faelschung), is("".getBytes()));
    }


    @Test
    public void shouldEncodeJsonCorrectly() throws Exception {

        MockHttpServletRequest original = new MockHttpServletRequest("PUT", "/some/uri");
        final String originalContent = contentAreaJson("testId");

        original.setContent(originalContent.getBytes());
        WrappedRequest wrapped = wrap(original);

        assertThat(extractContent(wrapped), is(originalContent.getBytes()));

    }

    private byte[] extractContent(WrappedRequest wrapped) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteStreams.copy(wrapped.getInputStream(), out);
        return out.toByteArray();
    }

    @Test
    public void shouldReadReqeuestWithAsciiEncoding() throws Exception {
        //given
        HttpServletRequest httpServletRequestMock = mock(HttpServletRequest.class);
        when(httpServletRequestMock.getMethod()).thenReturn("PUT");
        when(httpServletRequestMock.getRequestURI()).thenReturn("/some/uri");
        when(httpServletRequestMock.getCharacterEncoding()).thenReturn("ASCII");
        final String originalContent = contentAreaJson("testId");
        final InputStream inputStream = new ByteArrayInputStream(originalContent.getBytes("ASCII"));
        ServletInputStream servletInputStream = new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        };
        when(httpServletRequestMock.getInputStream()).thenReturn(servletInputStream);
        //when
        WrappedRequest wrapped = wrap(httpServletRequestMock);
        //then
        assertThat(extractContent(wrapped), is(originalContent.getBytes()));

    }

    private static String contentAreaJson(String id) {
        return "{ \n" +
                "  \"_id\": \"" + id + "\",\n" +
                "  \"name\": \"CA1\",\n" +
                "  \"contentRefs\": [\n" +
                "                    [{ \"_id\" : \"ContentRef1\", \"contentType\": \"SLIDESHOW_TEASER\" }],\n" +
                "                    [{ \"_id\" : \"ContentRef2\", \"contentType\": \"SHOPTEASER\" }]\n" +
                "                  ]\n" +
                "}";
    }



}
