package social.connectus.apigateway.filter;

import java.util.List;
import java.util.Objects;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import social.connectus.apigateway.jwt.JwtVerifier;

@Slf4j
@Component
public class CustomAuthFilter extends AbstractGatewayFilterFactory<CustomAuthFilter.Config> {

	private final JwtVerifier jwtVerifier;

	public CustomAuthFilter(JwtVerifier jwtVerifier) {
		super(Config.class);
		this.jwtVerifier = jwtVerifier;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();
			// filter가 적용되지 않는 request path(=endpoint)는 filter 적용에서 예외시킨다.
			// 대부분의 서비스는 authorization이 필요하기 때문에, 여집합으로 처리하였습니다.
			// ex) https://localhost:8800/user-service/login
			if (exchange.getRequest().getURI().getPath().matches(config.getExcludedFilterPathPattern())) {
				return chain.filter(exchange);
			}

			// Request Header 에 token 이 존재하지 않을 때
			if(!request.getHeaders().containsKey("Authorization")){
				return handleUnAuthorized(exchange); // 401 Error
			}

			// Request Header 에서 token 문자열 받아오기
			List<String> token = request.getHeaders().get("Authorization");
			String tokenString = Objects.requireNonNull(token).get(0);


			// 토큰 검증
			if(!jwtVerifier.verify(tokenString)) {
				return handleUnAuthorized(exchange); // 토큰이 일치하지 않을 때
			}

			// todo: 여기서 sanitize 작업 수행
			// 먼저, 모든 api의 endpoint를 확인하여 통일성을 갖추는 것이 급선무임

			return chain.filter(exchange); // 토큰이 일치할 때

		};
	}
	private Mono<Void> handleUnAuthorized(ServerWebExchange exchange) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(HttpStatus.UNAUTHORIZED);
		return response.setComplete();
	}

	public static class Config {
		// 예외 필터 경로 지정
		private String excludedFilterPathPattern;

		public String getExcludedFilterPathPattern() {
			return excludedFilterPathPattern;
		}

		public void setExcludedFilterPathPattern(String excludedFilterPathPattern) {
			this.excludedFilterPathPattern = excludedFilterPathPattern;
		}
	}
}
