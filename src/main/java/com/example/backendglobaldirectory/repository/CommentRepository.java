package com.example.backendglobaldirectory.repository;

import com.example.backendglobaldirectory.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("""
            select c from Comment c
                inner join Post p on c.post.id=p.id
            where p.id = :postId
            """)
    List<Comment> findCommentsByPostId(int postId);
}
