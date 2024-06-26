package social.connectus.infrastructure.databases;

import org.springframework.stereotype.Repository;

import static social.connectus.domain.model.QLikes.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import social.connectus.common.type.Type;
import social.connectus.domain.model.Likes;

@RequiredArgsConstructor
@Repository
public class LikeRepositoryCustomImpl implements LikeRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Long countByDomainIdAndType(Long domainId, Type type) {

		return jpaQueryFactory.select(likes.count())
			.from(likes)
			.where(likes.domainId.eq(domainId).and(likes.type.eq(type)))
			.fetchOne();
	}

	@Override
	public boolean existsByDomainId(Long domainId, Long userId, Type type) {
		Integer fetchFirst = jpaQueryFactory.selectOne()
			.from(likes)
			.where(likes.domainId.eq(domainId).and(likes.type.eq(type)).and(likes.userId.eq(userId)))
			.fetchOne();
		return fetchFirst != null;
	}

	@Override
	public List<Long> findAllByUserId(Long userId) {
		return jpaQueryFactory.select(likes.domainId)
			.from(likes)
			.where(likes.userId.eq(userId))
			.fetch();
	}
}
