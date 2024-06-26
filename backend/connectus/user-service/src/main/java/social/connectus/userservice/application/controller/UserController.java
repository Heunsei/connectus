package social.connectus.userservice.application.controller;

import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
import social.connectus.userservice.application.request.*;
import social.connectus.userservice.application.response.*;
import social.connectus.userservice.common.exception.NotFoundException;
import social.connectus.userservice.domain.command.PointChangeCommand;
import social.connectus.userservice.domain.port.inbound.AchievementUseCase;
import social.connectus.userservice.domain.port.inbound.PostUseCase;
import social.connectus.userservice.domain.port.inbound.UserUseCase;
import social.connectus.userservice.domain.port.inbound.WalkUseCase;
import social.connectus.userservice.domain.port.inbound.command.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

	private final UserUseCase userUseCase;
	private final AchievementUseCase achievementUseCase;
	private final WalkUseCase walkUseCase;
	private final PostUseCase postUseCase;

	@Operation(summary = "회원가입")
	@PostMapping("/register")
	public ResponseEntity<Void> registerUser(@RequestBody UserRegisterRequest userRegisterRequest) {
		userUseCase.register(UserRegisterCommand.from(userRegisterRequest));
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "로그인")
	@PostMapping("/login")
	public ResponseEntity<LoginUserResponse> loginUser(@RequestBody UserLoginRequest userLoginRequest) {
		return ResponseEntity.ok(userUseCase.login(UserLoginCommand.from(userLoginRequest)));
	}

	@Operation(summary = "로그아웃", deprecated = true)
	@PostMapping("/logout")
	public ResponseEntity<LogoutUserResponse> logoutUser(UserLogoutRequest userLogoutRequest) {
		return ResponseEntity.ok(userUseCase.logout(UserLogoutCommand.from(userLogoutRequest)));
	}

//	@Operation(summary = "유저 정보 받기", deprecated = true)
//	@GetMapping("/{userId}")
//	public ResponseEntity<GetUserResponse> getUser(@PathVariable)

	@Operation(summary = "특정 유저가 달성한 업적 조회")
	@GetMapping("/completed-achievement/{userId}")
	public ResponseEntity<CompletedAchievementListResponse> getUserCompletedAchievement(@PathVariable Long userId) {
		return ResponseEntity.ok().body(achievementUseCase.checkAchievement(userId));
	}

	// endWalk 때 statistics를 갱신하기 위한 controller 이번 refresh를 통해 새로 완료한 업적을 출력
	@Operation(summary = "endWalk 때 statistics를 갱신하기 위한 controller 이번 refresh를 통해 새로 완료한 업적을 출력")
	@PostMapping("/refresh-achievement")
	public ResponseEntity<List<AchievementResponse>> refreshAchievement(Long userId,
		@RequestBody RefreshAchievementRequest statistics) {
		return ResponseEntity.ok(achievementUseCase.refreshAchievement(userId, statistics).getCompletedAchievementInThisRequest());
	}

	@Operation(summary = "내 산책 기록 조회")
	@GetMapping("/me/walk")
	public ResponseEntity<MyWalkResponse> getMyWalk(MyWalkRequest request) {
		return ResponseEntity.ok(walkUseCase.getMyWalk(MyWalkCommand.from(request)));
	}

	@Operation(summary = "내가 좋아한 경로 조회")
	@GetMapping("/route/like")
	public ResponseEntity<MyPreferenceRouteResponse> getMyPreferenceRoute(MyPreferenceRouteRequest request) {
		MyPreferenceRouteCommand command = MyPreferenceRouteCommand.from(request);
		return ResponseEntity.ok(walkUseCase.getMyPreferenceRoute(command));
	}

	@Operation(summary = "좋아요 누른 방명록 확인")
	@GetMapping("/post/like")
	public ResponseEntity<MyPreferencePostResponse> getMyPreferencePost(MyPreferencePostRequest request) {
		MyPreferencePostCommand command = MyPreferencePostCommand.from(request);
		return ResponseEntity.ok(postUseCase.getMyPreferencePost(command));
	}

	@GetMapping("/{userId}/openedPosts")
	public ResponseEntity<OpenedPostResponse> getOpenedPost(@PathVariable Long userId) throws JsonProcessingException {
		return ResponseEntity.ok(userUseCase.getOpenedPost(userId));
	}

	@PostMapping("/{userId}/openedPost")
	public ResponseEntity<Void> updateOpenedPost(@PathVariable Long userId, @RequestBody Long postId) {
		userUseCase.updateOpenedPost(userId, postId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{userId}/openedPosts")
	public ResponseEntity<Void> updateOpenedPosts(@PathVariable Long userId, @RequestBody List<Long> postIdList) {
		userUseCase.updateOpenedPosts(userId, postIdList);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/{userId}/get-author-info")
	public ResponseEntity<UserResponseForPost> getUserForPost(@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(userUseCase.getUserResponseForPost(userId));
	}

	@PostMapping("/{userId}/update-avatar")
	public ResponseEntity<String> updateAvatar(@PathVariable("userId") Long userId, @RequestBody UpdateAvatarRequest request) {
		return ResponseEntity.ok(userUseCase.updateAvatar(userId, request));
	}

	@PostMapping("/insert-spot")
	public ResponseEntity<ChangePositionResponse> insertUserPosition(@RequestBody CreateUserPositionRequest request) {
		try{
			log.info("Log insertUserPosition request : userId->"+request.getUserId()+", latitude->"+request.getLatitude()
			+", longitude->"+request.getLongitude());
			ChangePositionResponse resp = userUseCase.insertUserPosition(request);
			if(resp == null) return ResponseEntity.badRequest().build();
			return ResponseEntity.ok(resp);
		}catch (NotFoundException e){
			return ResponseEntity.notFound().build();
		}catch (NullPointerException e){
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/update-spot")
	public ResponseEntity<ChangePositionResponse> updateUserPosition(@RequestBody CreateUserPositionRequest request) {
		try {
			log.info("Log updateUserPosition request : userId->"+ request.getUserId()+", latitude->"+request.getLatitude()+
					", longitude->"+request.getLongitude());
			return ResponseEntity.ok(userUseCase.updateUserPosition(request));
		}catch (NotFoundException e){
			return ResponseEntity.notFound().build();
		}catch (NullPointerException e){
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/delete-spot/{userId}")
	public ResponseEntity<String> deleteUserPosition(@PathVariable Long userId) {
		try{
			log.info("Log deleteUserPosition request : userId->"+userId);
			userUseCase.deleteUserPosition(userId);
			return ResponseEntity.ok().body("Saved.");
		}catch (NotFoundException e){
			return ResponseEntity.notFound().build();
		}catch (NullPointerException e){
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/increase-point")
	public ResponseEntity<PointResponse> increasePoint(@RequestBody PointChangeRequest request) {
		return ResponseEntity.ok(userUseCase.increasePoint(PointChangeCommand.from(request)));
	}

	@PostMapping("/decrease-point")
	public ResponseEntity<PointResponse> decreasePoint(@RequestBody PointChangeRequest request) {
		return ResponseEntity.ok(userUseCase.decreasePoint(PointChangeCommand.from(request)));
	}

	@PostMapping("/insert/post-history")
	public ResponseEntity<PointResponse> insertPostHistory(@RequestBody InsertPostRequest request) {
		return ResponseEntity.ok(userUseCase.insertPostHistory(request));
	}

	@GetMapping("/info/{userId}")
	public ResponseEntity<UserInfoResponse> getUserInfo(@PathVariable Long userId) {
		return ResponseEntity.ok(userUseCase.getUserInfo(userId));
	}

	@GetMapping("/likes/{userId}")
	public ResponseEntity<LikeResponse> getMyLikeList(@PathVariable Long userId) {
		return ResponseEntity.ok(userUseCase.getMyLikeList(userId));
	}
}
