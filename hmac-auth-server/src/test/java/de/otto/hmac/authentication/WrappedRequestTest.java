package de.otto.hmac.authentication;


import org.springframework.mock.web.MockHttpServletRequest;
import org.testng.annotations.Test;

import static de.otto.hmac.authentication.WrappedRequest.wrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

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

        WrappedRequest fälschung = wrap(original);
        assertThat(fälschung.getBody(), is(originalBody));
    }


    @Test
    public void shouldNotChangeRequestBodyWithUmlauts() throws Exception {
        MockHttpServletRequest original = new MockHttpServletRequest("PUT", "/some/uri");
        String originalBody = "{ \"some\" : \"jsön\" }";
        original.setContent(originalBody.getBytes());

        WrappedRequest fälschung = wrap(original);
        assertThat(fälschung.getBody(), is(originalBody));
    }

    @Test
    public void shouldHaveEmptyBodyWhenEmpty() throws Exception {
        MockHttpServletRequest original = new MockHttpServletRequest("PUT", "/some/uri");

        WrappedRequest fälschung = wrap(original);

        assertThat(fälschung.getBody(), is(""));
    }


    @Test
    public void shouldEncodeJsonCorrectly() throws Exception {

        MockHttpServletRequest original = new MockHttpServletRequest("PUT", "/some/uri");
        final String originalContent = contentAreaJson("testId");

        original.setContent(originalContent.getBytes());
        WrappedRequest wrapped = wrap(original);


        assertThat(wrapped.getBody().getBytes(), is(originalContent.getBytes()));

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
