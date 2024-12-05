package fi.invian.codingassignment.repository;

import fi.invian.codingassignment.dto.MessageResponse;
import fi.invian.codingassignment.dto.StatisticsResponse;
import fi.invian.codingassignment.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
	
	// Fetch all messages for a specific recipient
	@Query("""
			SELECT new fi.invian.codingassignment.dto.MessageResponse(
			m.id, m.title, m.body, u.name, m.sentAt
			)
			FROM Message m
			JOIN m.recipients r
			JOIN User u ON m.sender.id = u.id
			WHERE r.recipient.id = :recipientId
			ORDER BY m.sentAt DESC
			""")
	Page<MessageResponse> findMessagesByRecipientId(
			@Param("recipientId") Long recipientId,
			Pageable pageable
	);
	
	// Fetch top 10 senders by sent message count for the last 30 days
	@Query("""
        SELECT new fi.invian.codingassignment.dto.StatisticsResponse(
            u.id, u.name, COUNT(m.id)
        )
        FROM Message m
        JOIN User u ON m.sender.id = u.id
        WHERE m.sentAt >= :fromDate
        GROUP BY u.id, u.name
        ORDER BY COUNT(m.id) DESC
        """)
	List<StatisticsResponse> findTopSenders(@Param("fromDate") Instant fromDate, Pageable pageable);
}