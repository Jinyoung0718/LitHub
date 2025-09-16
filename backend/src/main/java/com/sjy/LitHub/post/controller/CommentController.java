package com.sjy.LitHub.post.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjy.LitHub.global.model.BaseResponse;
import com.sjy.LitHub.global.model.Empty;
import com.sjy.LitHub.global.model.PageResponse;
import com.sjy.LitHub.post.model.req.CommentCreateRequestDTO;
import com.sjy.LitHub.post.model.res.comment.CommentResponseDTO;
import com.sjy.LitHub.post.model.res.comment.ReplyCommentDTO;
import com.sjy.LitHub.post.model.res.comment.RootCommentDTO;
import com.sjy.LitHub.post.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@SecurityRequirement(name = "accessToken")
@Tag(name = "댓글 관리", description = "댓글 생성, 조회, 수정, 삭제 API")
public class CommentController {

	private final CommentService commentService;

	@Operation(summary = "루트 댓글 조회 (페이징)", description = "게시글의 루트 댓글을 페이징으로 조회합니다.")
	@GetMapping("/posts/{postId}/comments")
	public BaseResponse<PageResponse<RootCommentDTO>> getRootComments(
		@PathVariable Long postId,
		Pageable pageable
	) {
		return BaseResponse.success(commentService.getRootComments(postId, pageable));
	}

	@Operation(summary = "대댓글 조회 (페이징)", description = "특정 루트 댓글의 대댓글을 페이징으로 조회합니다.")
	@GetMapping("/comments/{commentId}/replies")
	public BaseResponse<PageResponse<ReplyCommentDTO>> getReplies(
		@PathVariable Long commentId,
		Pageable pageable
	) {
		return BaseResponse.success(commentService.getRepliesComments(commentId, pageable));
	}

	@Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다. 대댓글도 지원됩니다.")
	@PostMapping("/posts/{postId}/comments")
	public BaseResponse<CommentResponseDTO> createComment(
		@PathVariable Long postId,
		@RequestBody @Valid CommentCreateRequestDTO request
	) {
		return BaseResponse.success(commentService.createComment(postId, request));
	}

	@Operation(summary = "댓글 수정", description = "본인의 댓글을 수정합니다.")
	@PatchMapping("/comments/{commentId}")
	public BaseResponse<CommentResponseDTO> updateComment(
		@PathVariable Long commentId,
		@RequestBody @Valid CommentCreateRequestDTO request
	) {
		return BaseResponse.success(commentService.updateComment(commentId, request));
	}

	@Operation(summary = "댓글 삭제", description = "본인의 댓글을 삭제합니다.")
	@DeleteMapping("/comments/{commentId}")
	public BaseResponse<Empty> deleteComment(
		@PathVariable Long commentId
	) {
		commentService.deleteComment(commentId);
		return BaseResponse.success();
	}
}