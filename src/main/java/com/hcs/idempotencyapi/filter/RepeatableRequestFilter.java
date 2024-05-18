package com.hcs.idempotencyapi.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@Component
public class RepeatableRequestFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		log.debug("repeatable request body filter");
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		chain.doFilter(new RepeatableReadRequestBodyWrapper(httpServletRequest), response);
	}

}
