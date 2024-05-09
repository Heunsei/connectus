package social.connectus.walk.infrastructure.databases.mariadb;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;
import social.connectus.walk.common.constants.WalkConstants;
import social.connectus.walk.common.exception.ResourceNotFoundException;
import social.connectus.walk.domain.command.*;
import social.connectus.walk.domain.model.entity.*;
import social.connectus.walk.domain.ports.outbound.WalkPort;
import social.connectus.walk.infrastructure.databases.mariadb.repository.WalkRepository;
import social.connectus.walk.infrastructure.external.FeignClient;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WalkAdapter implements WalkPort {
    private final FeignClient feignClient;

    private final ModelMapper modelMapper;
    private final WalkRepository walkRepository;

    public String feignHealthCheck(){
        return feignClient.healthCheck();
    }

    @Override
    public Walk getWalkById(long walkId) {
        return walkRepository.findById(walkId)
                .orElseThrow(()-> new ResourceNotFoundException(WalkConstants.WALK_NOT_FOUND + walkId));
    }

    @Override
    public List<Walk> getWalkByUser(long userId) {
        return walkRepository.findByUser(userId);
    }

    @Transactional
    @Override
    public void routeShare(RouteShareCommand command) {
        long walkId = command.getWalkId();
        Walk walk = getWalkById(walkId);
        walk.setPublic(true);
    }

    @Transactional
    @Override
    public void routeProtect(RouteProtectCommand command) {
        long walkId = command.getWalkId();
        Walk walk = getWalkById(walkId);
        walk.setPublic(false);
    }

    @Override
    public Slice<Long> getWalksByPosition(GetWalksByPositionCommand command) {
//        findSliceByPosition(Position position, double distance, long userId, Pageable pageable)
//        double distance = command.getDistance();
//        long userId = command.getUserId();
//        PageRequest pageRequest = PageRequest.of(command.getPageNumber(), command.getPageSize(), Sort.by("createdAt").descending());
//        return walkRepository.findSliceByPosition(userPosition, distance, userId, pageRequest);
        return null;
    }

    @Transactional
    @Override
    public void routeTrack(RouteTrackCommand command) {
        Walk walk = getWalkById(command.getWalkId());
        walk.getTrackingUsers().add(TrackingUser.builder().userId(command.getUserId()).walk(walk).build());
    }

    @Override
    @Transactional
    public Walk createWalk(CreateWalkCommand command) {
        Walk walk = Walk.builder()
                .userId(command.getUserId())
                .title(command.getTitle())
                .route(command.getRoute())
                .walkDistance(command.getWalkDistance())
                .walkTime(command.getWalkTime())
                .completedAchievement(command.getCompletedAchievement())
                .participateEvent(command.getParticipateEvent())
                .isPublic(command.isPublic())
                .build();

        createRoute(walk.getRoute(), walk);
        createAchievement(walk.getCompletedAchievement(), walk);
        walkRepository.save(walk);
        return walk;
    }

//    @Transactional
    @Override
    public void createRoute(List<Route> routes, Walk walk){
        routes.forEach(route -> route.setWalk(walk));
//        walkRepository.save(routes);
    }

//    @Transactional
    @Override
    public void createAchievement(Set<CompletedAchievement> completedAchievements, Walk walk){
        completedAchievements.forEach(achievement -> achievement.setWalk(walk));
//        walkRepository.save(completedAchievements);
    }

    @Override
    @Transactional
    public void routeLike(RouteLikeCommand command) {
        long walkId = command.getWalkId();
        long userId = command.getUserId();
        Walk walk = getWalkById(walkId);

        walk.getLikeUsers().add(LikeUser.builder().userId(userId).walk(walk).build());
    }
}
