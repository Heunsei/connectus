package social.connectus.infrastructure.adapter;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import social.connectus.application.rest.response.LikesResponse;
import social.connectus.common.type.Type;
import social.connectus.domain.model.Likes;
import social.connectus.domain.ports.outbound.LikePort;
import social.connectus.infrastructure.databases.LikeRepository;

@Component
@RequiredArgsConstructor
public class LikeAdapter implements LikePort {
	private final LikeRepository likeRepository;

	@Transactional
	@Override
	public String insertLike(Long domainId, Long userId, Type type) {
		Optional<Likes> isPresentLikes = likeRepository.findByUserIdAndDomainIdAndType(userId, domainId, type);
		if(isPresentLikes.isPresent()) {
			Likes likes = isPresentLikes.get();
			likeRepository.delete(likes);
			return "delete likes";
		}
		else {
			likeRepository.save(Likes.of(userId, domainId, type));
			return "save likes";
		}
	}

	@Override
	public int getLikeCount(Long domainId, Type type) {
		return likeRepository.countByDomainIdAndType(domainId, type).intValue();
	}

	@Override
	public boolean isLike(Long postId, Long userId, Type type) {
		return likeRepository.existsByDomainId(postId, userId, type);
	}

	@Override
	public List<Long> getLikeList(Long userId) {
		return likeRepository.findAllByUserId(userId);
	}
}
