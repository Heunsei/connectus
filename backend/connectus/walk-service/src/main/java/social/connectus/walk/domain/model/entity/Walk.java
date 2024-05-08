package social.connectus.walk.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import social.connectus.walk.domain.model.VO.Position;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Walk extends BaseEntity{
    // 산책 기록 entity라는 것을 명심하기
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walkId;

    @Column(nullable = false)
    private Long userId;

    private String title;

    @Embedded
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Position> route;

    private int walkTime;

    private int walkDistance;   // 산책 거리

    @Column(nullable = false)
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Long> likeUsers;

    @ElementCollection(fetch = FetchType.LAZY)
    private List<Long> completedAchievement;

    private Long participateEvent;

}
