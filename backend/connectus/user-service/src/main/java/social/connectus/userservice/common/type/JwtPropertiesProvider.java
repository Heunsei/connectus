package social.connectus.userservice.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JwtPropertiesProvider {

	EMAIL("email"),
	ISSUER("user-service"),
	USER_ID("userId"),
	PAYLOAD("payload");

	private final String value;
}
