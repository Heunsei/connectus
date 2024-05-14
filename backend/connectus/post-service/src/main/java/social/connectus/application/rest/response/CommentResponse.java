package social.connectus.application.rest.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import social.connectus.domain.model.RDBMS.Comment;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {
	private Long commentId;
	private String content;
	private Long authorId;
	private String authorName;
	private LocalDateTime createdAt;

	public static CommentResponse from(Comment comment){
		return CommentResponse.builder()
			.commentId(comment.getId())
			.authorId(comment.getAuthorId())
			.content(comment.getContent())
			.createdAt(comment.getCreatedAt())
			.build();
	}
}
