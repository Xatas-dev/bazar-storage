package org.bazar.bazarstorage.it.testutil;

import builder.JwtBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.LinkedMultiValueMap;

import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class RestTestUtil {
    @Autowired
    private MockMvc mvc;

    private final ObjectMapper mapper = TestDataTransformUtil.getTestObjectMapper();

    public <T> T getPerform(
            String url,
            Map<String, Object> params,
            TypeReference<T> typeReference,
            Map<String, List<String>> headers,
            ResultMatcher httpStatus
    ) throws Exception {
        MockHttpServletRequestBuilder request = buildGetRequest(url, params, headers);
        MockHttpServletResponse response = perform(request).andExpect(httpStatus).andReturn().getResponse();
        return getDtoFromResponse(response, typeReference);
    }

    public <T> T postPerform(
            String url,
            Map<String, Object> params,
            Object body,
            TypeReference<T> typeReference,
            Map<String, List<String>> headers,
            ResultMatcher httpStatus
    ) throws Exception {
        MockHttpServletRequestBuilder request = buildPostRequest(url, params, headers, body);
        MockHttpServletResponse response = perform(request).andExpect(httpStatus).andReturn().getResponse();
        return getDtoFromResponse(response, typeReference);
    }

    public <T> T deletePerform(
            String url,
            Map<String, Object> params,
            Object body,
            TypeReference<T> typeReference,
            Map<String, List<String>> headers,
            ResultMatcher httpStatus
    ) throws Exception {
        MockHttpServletRequestBuilder request = buildDeleteRequest(url, params, headers, body);
        MockHttpServletResponse response = perform(request).andExpect(httpStatus).andReturn().getResponse();
        return getDtoFromResponse(response, typeReference);
    }


    public <T> T patchPerform(
            String url,
            Map<String, Object> params,
            Object body,
            TypeReference<T> typeReference,
            Map<String, List<String>> headers,
            ResultMatcher httpStatus) throws Exception {
        MockHttpServletRequestBuilder request = buildPatchRequest(url, params, headers, body);
        MockHttpServletResponse response = perform(request).andExpect(httpStatus).andReturn().getResponse();
        return getDtoFromResponse(response, typeReference);
    }

    // =================================================================================================================
    // = Implementation
    // =================================================================================================================

    private <T> T getDtoFromResponse(MockHttpServletResponse response, TypeReference<T> typeReference) throws Exception {
        String body = response.getContentAsString();

        if (body.isBlank()) {
            return null;
        }

        if (typeReference.getType().equals(String.class)) {
            @SuppressWarnings("unchecked")
            T result = (T) body;
            return result;
        }

        return mapper.readValue(body, typeReference);
    }

    private MockHttpServletRequestBuilder buildGetRequest(
            String url, Map<String, Object> params, Map<String, List<String>> headers
    ) {
        MockHttpServletRequestBuilder requestMock = get(url).contentType(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            requestMock.param(entry.getKey(), String.valueOf(entry.getValue()));
        }

        if (!headers.isEmpty()) {
            requestMock.headers(new HttpHeaders(new LinkedMultiValueMap<>(headers)));
        }

        return requestMock;
    }

    private MockHttpServletRequestBuilder buildPostRequest(
            String url, Map<String, Object> params, Map<String, List<String>> headers, Object body
    ) throws Exception {
        MockHttpServletRequestBuilder requestMock = post(url).contentType(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            requestMock.param(entry.getKey(), String.valueOf(entry.getValue()));
        }

        if (!headers.isEmpty()) {
            requestMock.headers(new HttpHeaders(new LinkedMultiValueMap<>(headers)));
        }

        requestMock.content(toJsonString(body));

        return requestMock;
    }

    private MockHttpServletRequestBuilder buildDeleteRequest(
            String url, Map<String, Object> params, Map<String, List<String>> headers, Object body
    ) throws Exception {
        MockHttpServletRequestBuilder requestMock = delete(url).contentType(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            requestMock.param(entry.getKey(), String.valueOf(entry.getValue()));
        }

        if (!headers.isEmpty()) {
            requestMock.headers(new HttpHeaders(new LinkedMultiValueMap<>(headers)));
        }

        requestMock.content(toJsonString(body));

        return requestMock;
    }

    private MockHttpServletRequestBuilder buildPatchRequest(
            String url, Map<String, Object> params, Map<String, List<String>> headers, Object body
    ) throws Exception {
        MockHttpServletRequestBuilder requestMock = patch(url).contentType(MediaType.APPLICATION_JSON);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            requestMock.param(entry.getKey(), String.valueOf(entry.getValue()));
        }

        if (!headers.isEmpty()) {
            requestMock.headers(new HttpHeaders(new LinkedMultiValueMap<>(headers)));
        }

        requestMock.content(toJsonString(body));

        return requestMock;
    }

    private String toJsonString(Object dto) throws JsonProcessingException {
        return mapper.writeValueAsString(dto);
    }

    private ResultActions perform(MockHttpServletRequestBuilder requestBuilder) throws Exception {
        return mvc.perform(requestBuilder.with(jwt().jwt(JwtBuilder.buildDefault())));
    }
}
