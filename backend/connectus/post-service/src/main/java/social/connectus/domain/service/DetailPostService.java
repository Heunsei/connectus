package social.connectus.domain.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import social.connectus.application.rest.request.CoordinateRequestDto;
import social.connectus.application.rest.response.DetailPostResponse;
import social.connectus.application.rest.response.GetPostSpotResponse;
import social.connectus.common.annotation.UseCase;
import social.connectus.common.exception.BusinessException;
import social.connectus.common.exception.GlobalException;
import social.connectus.common.exception.NotFoundException;
import social.connectus.common.exception.ParameterNotFoundException;
import social.connectus.domain.ports.inbound.DetailPostUseCase;
import social.connectus.domain.ports.outbound.DetailPostPort;

@UseCase
@RequiredArgsConstructor
public class DetailPostService implements DetailPostUseCase {
	private final DetailPostPort detailPostPort;
	private final int R = 20;
	@Override
	public DetailPostResponse detailByUserExperience(Long postId, Long userId) throws GlobalException,
			BusinessException, NotFoundException {
		try {
			if(postId == null) {
				throw new ParameterNotFoundException("authorId");
			}
			if(userId == null) {
				throw new ParameterNotFoundException("userId");
			}
			List<Long> openedPostListByUserId = detailPostPort.openedPostByUserId(userId).getOpenedPostList();
			if (!openedPostListByUserId.contains(postId)) {
				throw new BusinessException("You haven't seen this post.");
			}
		} catch (Exception e) {
			throw new GlobalException("DetailPostService : " + e.getMessage());
		}

		return detailPostPort.detailPost(postId, userId);
	}

//	@Override
//	public DetailPostResponse detailByLocation(Long postId, CoordinateRequestDto userLocation) throws GlobalException, BusinessException {
//		CoordinateRequestDto postLocation = detailPostPort.postSpotByPostId(postId);
//		try {
//			if(postId == null) {
//				throw new ParameterNotFoundException("postId");
//			}
//			// TODO : R 크기 정해지면 수정
//			if(getDistance(userLocation, postLocation) < R) {
//				throw new BusinessException("post is too far");
//			}
//		} catch (Exception e) {
//			throw new GlobalException("DetailPostService : " + e.getMessage());
//		}
//		DetailPostResponse response = detailPostPort.detailPost(postId);
//		return response;
//	}

	@Override
	public DetailPostResponse detailByPostId(Long postId, Long userId, Double distance) throws
			GlobalException, BusinessException, NotFoundException {
		List<Long> openedPostListByUserId = detailPostPort.openedPostByUserId(userId).getOpenedPostList();
		try{
			if(openedPostListByUserId == null ){
				throw new BusinessException("can't contact userService");
			}
			if(openedPostListByUserId.contains(postId)) {
				return detailPostPort.detailPost(postId, userId);
			} else {
				if(distance < 0.1) {
					detailPostPort.updateOpenedPost(userId, postId);
					return detailPostPort.detailPost(postId, userId);
				}
				else {
					return detailPostPort.samplePost(postId, userId);
			}
			}
		}catch (NotFoundException e){
			throw new NotFoundException(e.getMessage());
		}
		catch (Exception e) {
			throw new GlobalException("DetailPostService : " + e.getMessage());
		}
	}

	@Override
	public String healthCheck() {
		return detailPostPort.healthCheck();
	}

	@Override
	public GetPostSpotResponse getPostSpotByPostId(Long postId) throws NotFoundException {
		return detailPostPort.getPostSpotById(postId);
	}


}
