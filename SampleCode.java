@GetMapping("/{id}/comments")
public ResponseEntity<List<CommentResponse>>
getArticleComments(
        @PathVariable Long id) {

    List<CommentResponse> comments =
            articleService.getCommentsByArticle(id);

    return ResponseEntity.ok(comments);
}