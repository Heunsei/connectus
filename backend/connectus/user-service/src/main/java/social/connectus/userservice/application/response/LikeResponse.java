package social.connectus.userservice.application.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponse {
	List<Long> likedPostIdList;
	List<Long> likedWalkIdList;
}
