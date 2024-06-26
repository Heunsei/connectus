package social.connectus.userservice.domain.port.inbound.command;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import social.connectus.userservice.application.request.UserRegisterRequest;

/**
 * 모든 검증이 완료된 신뢰할 수 있는 데이터 값들
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterCommand {
	private String email;
	private String password;
	private String phoneNumber;
	private String nickname;
	private String name;
	private LocalDate birthday;
	private String imageUrl;

	public static UserRegisterCommand from(UserRegisterRequest request) {
		String imageUrl = request.getImageUrl();
		if(imageUrl == null) {
			imageUrl = "https://e106-connectus.s3.ap-northeast-2.amazonaws.com/avatar/character_1.png";
		}
		// request -> command
		// todo: validate input value, encrypt user password
		return UserRegisterCommand.builder()
			.email(request.getEmail())
			.password(request.getPassword())
			.phoneNumber(request.getPhoneNumber())
			.nickname(request.getNickname())
			.name(request.getName())
			.birthday(request.getBirthday())
			.imageUrl(imageUrl)
			.build();
	}
}
