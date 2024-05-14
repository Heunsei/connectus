package social.connectus.walk.infrastructure.external;

import org.springframework.web.bind.annotation.GetMapping;
import social.connectus.walk.application.rest.response.AchievementIdResponse;
import social.connectus.walk.application.rest.response.AchievementResponse;
import social.connectus.walk.domain.command.GetAchievementsCommand;

import java.util.List;

@org.springframework.cloud.openfeign.FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/refresh-achievement")
    List<AchievementResponse> getAchievementsByWalk(long userId, GetAchievementsCommand command);
}
