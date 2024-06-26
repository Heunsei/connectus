package social.connectus.walk.domain.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Walk extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="walk_id")
    private Long id;

    @Column(nullable = false)
    private Long userId;

    private String title;

    @OneToMany(mappedBy = "walk", cascade = CascadeType.ALL)
    private List<Route> route;

    private int walkTime;

    private double walkDistance;   // 산책 거리

    @OneToMany(mappedBy = "walk", cascade = CascadeType.ALL)
    List<Post> postList;

    @OneToMany(mappedBy = "walk", cascade = CascadeType.ALL)
    private Set<LikeUser> likeUsers;

    @ElementCollection
    private List<String> achievementCode;

    private Long participateEvent;

    @OneToMany(mappedBy = "walk", cascade = CascadeType.ALL)
    private Set<TrackingUser> trackingUsers;

    @ColumnDefault("false")
    @Column(nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isPublic;

    private String imageUrl;

}
