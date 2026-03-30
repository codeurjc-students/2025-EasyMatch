package es.codeurjc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import es.codeurjc.model.ChatMessage;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long>{
    @Query("""
        SELECT cm FROM ChatMessage cm
        JOIN cm.match m
        LEFT JOIN m.team1Players p1
        LEFT JOIN m.team2Players p2
        WHERE p1.id = :userId OR p2.id = :userId
        ORDER BY cm.timestamp DESC
    """)
    List<ChatMessage> findMessagesByUser(@Param("userId") Long userId);
    List<ChatMessage> findByMatchId(long matchId);
}
