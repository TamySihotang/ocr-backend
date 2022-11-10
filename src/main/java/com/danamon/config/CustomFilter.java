package com.danamon.config;

import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class CustomFilter extends GenericFilter {

    private final String excludeUri;
    private final List<String> excludeWildcard;

    public CustomFilter(String excludeUri, List<String> excludeWildcard) {
        this.excludeUri = excludeUri;
        this.excludeWildcard = excludeWildcard;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String uri = httpServletRequest.getRequestURI();

        if (isExcludedUri(uri)) {
            chain.doFilter(request, response);
        } else {
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        }

    }

    private boolean isExcludedUri(String uri) {
        return excludeUri.contains(uri) || isExcludeWildcard(uri);
    }

    private boolean isExcludeWildcard(String uri) {

        final boolean[] isExcluded = {false};

        excludeWildcard.forEach(wildcard -> {
            if (uri.contains(wildcard)) {
                isExcluded[0] = true;
            }
        });

        return isExcluded[0];
    }
}
