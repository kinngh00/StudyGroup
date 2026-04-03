package com.example.study.global.config.security;

import com.example.study.domain.auth.repository.AccessTokenBlacklistRepository;
import com.example.study.domain.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_PREFIX = "Bearer ";

  private final JwtTokenProvider jwtTokenProvider;
  private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(
      HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      FilterChain filterChain
  ) throws ServletException, IOException {
    String accessToken = resolveAccessToken(httpServletRequest);

    if (StringUtils.hasText(accessToken)
        && jwtTokenProvider.validateToken(accessToken)
        && jwtTokenProvider.isAccessToken(accessToken)
        && !accessTokenBlacklistRepository.existsById(accessToken)) {

      Long userId = jwtTokenProvider.getUserId(accessToken);
      if (userRepository.existsById(userId)) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    }

    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

  private String resolveAccessToken(HttpServletRequest httpServletRequest) {
    String authorizationHeader = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
    if (!StringUtils.hasText(authorizationHeader)) {
      return null;
    }

    if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
      return null;
    }

    return authorizationHeader.substring(BEARER_PREFIX.length());
  }
}
