@PutMapping("/admin/users/{userId}/block")
public ResponseEntity<String> blockUser(
        @PathVariable Long userId) {

    service.blockUser(userId);

    return ResponseEntity.ok(
            "User blocked successfully."
    );
}

public void blockUser(Long userId) {

    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new RuntimeException(
                            "User not found with id: " + userId
                    )
            );

    user.setStatus(0);

    userRepository.save(user);
}