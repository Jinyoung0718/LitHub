package com.sjy.LitHub.post.controller;

import java.util.List;

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
import com.sjy.LitHub.post.model.req.CommentCreateRequestDTO;
import com.sjy.LitHub.post.model.res.comment.CommentResponseDTO;
import com.sjy.LitHub.post.model.res.comment.CommentTreeDTO;
import com.sjy.LitHub.post.service.CommentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/comments")
@SecurityRequirement(name = "accessToken")
@Tag(name = "댓글 관리", description = "댓글 생성, 수정, 삭제 API")
public class CommentController {

	private final CommentService commentService;

	@Operation(summary = "게시글 댓글 계층 조회", description = "게시글의 댓글과 대댓글을 계층 구조로 조회합니다.")
	@GetMapping("/post/{postId}")
	public BaseResponse<List<CommentTreeDTO>> getCommentTree(@PathVariable Long postId) {
		List<CommentTreeDTO> commentTree = commentService.getCommentTree(postId);
		return BaseResponse.success(commentTree);
	}

	@Operation(summary = "댓글 작성", description = "게시글에 댓글을 작성합니다. 대댓글도 지원됩니다.")
	@PostMapping("/{postId}")
	public BaseResponse<CommentResponseDTO> createComment(
		@PathVariable Long postId,
		@RequestBody @Valid CommentCreateRequestDTO request
	) {
		return BaseResponse.success(commentService.createComment(postId, request));
	}

	@Operation(summary = "댓글 수정", description = "본인의 댓글을 수정합니다.")
	@PatchMapping("/{commentId}")
	public BaseResponse<CommentResponseDTO> updateComment(
		@PathVariable Long commentId,
		@RequestBody @Valid CommentCreateRequestDTO request
	) {
		return BaseResponse.success(commentService.updateComment(commentId, request));
	}

	@Operation(summary = "댓글 삭제", description = "본인의 댓글을 삭제합니다.")
	@DeleteMapping("/{commentId}")
	public BaseResponse<Empty> deleteComment(
		@PathVariable Long commentId
	) {
		commentService.deleteComment(commentId);
		return BaseResponse.success();
	}
}