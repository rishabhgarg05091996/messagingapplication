package fi.invian.codingassignment.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
public class Message {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id", nullable = false)
	private User sender;
	
	@Column(nullable = false, length = 255)
	private String title;
	
	@Column(nullable = false, columnDefinition = "TEXT")
	private String body;
	
	@Column(name = "sent_at", nullable = false)
	private Instant sentAt;
	
	@OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<MessageRecipient> recipients = new ArrayList<>();
	
	public void addRecipient(User recipient) {
		MessageRecipient messageRecipient = new MessageRecipient(this, recipient);
		messageRecipient.setMessage(this);
		recipients.add(messageRecipient);
	}
}