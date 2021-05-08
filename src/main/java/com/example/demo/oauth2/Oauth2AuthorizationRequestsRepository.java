package com.example.demo.oauth2;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;

public class Oauth2AuthorizationRequestsRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest>{

	private static final String clientId = "697702354184763";
	
	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		Map<String, String> cookieHolder = new HashMap<>();
		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if (cookie.getName().equalsIgnoreCase("authorizationRequestUri")) {
				cookieHolder.put("authorizationRequestUri", cookie.getValue());
			} else if (cookie.getName().equalsIgnoreCase("authorizationUri")) {
				cookieHolder.put("authorizationUri", cookie.getValue());
			} else if (cookie.getName().equalsIgnoreCase("authorizationRedirectUri")) {
				cookieHolder.put("authorizationRedirectUri", cookie.getValue());
			}
		}
		
		Map<String, Object> attributes = new HashMap<>();
		attributes.put(OAuth2ParameterNames.REGISTRATION_ID, clientId);
		OAuth2AuthorizationRequest oauth2AuthorizationRequest = OAuth2AuthorizationRequest.authorizationCode()
				.attributes(attributes)
				.authorizationUri(cookieHolder.get("authorizationUri"))
				.authorizationRequestUri(cookieHolder.get("authorizationRequestUri"))
				.redirectUri(cookieHolder.get("authorizationRedirectUri"))
				.build();
		
		
		return oauth2AuthorizationRequest;
	}

	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
			HttpServletResponse response) {
		String authorizationRequestUri = authorizationRequest.getAuthorizationRequestUri();
		String authorizationUri = authorizationRequest.getAuthorizationUri();
		String redirectUri = authorizationRequest.getRedirectUri();
		
		Cookie authorizationRequestUriCookie = new Cookie("authorizationRequestUri", authorizationRequestUri);
		Cookie authorizationUriCookie = new Cookie("authorizationUri", authorizationUri);
		Cookie authorizationRedirectUriCookie = new Cookie("redirectUri", redirectUri);
		
		response.addCookie(authorizationRequestUriCookie);
		response.addCookie(authorizationUriCookie);
		response.addCookie(authorizationRedirectUriCookie);
		
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
		return this.loadAuthorizationRequest(request);
	}


	
	
}
