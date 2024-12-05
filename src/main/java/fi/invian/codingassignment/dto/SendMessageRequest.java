package fi.invian.codingassignment.dto;

import fi.invian.codingassignment.entity.Message;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class SendMessageRequest {
	
	@Positive(message = "Sender ID must be a positive number.")
	@NotNull(message = "Sender ID must not be empty.")
	private Long senderId;
	
	@NotBlank(message = "Title cannot be empty.")
	@Size(max = 255, message = "Title cannot exceed 255 characters.")
	private String title;
	
	@NotBlank(message = "Body cannot be empty.")
	private String body;
	
	@NotEmpty(message = "Recipient IDs must not be empty.")
	@Size(max = 5, message = "A message can have a maximum of 5 recipients.")
	private List<@Positive(message = "Recipient IDs must be positive numbers.") Long> recipientIds;
	
	// Convert DTO to Entity
	public Message toMessageEntity() {
		Message message = new Message();
		message.setTitle(this.title);
		message.setBody(this.body);
		message.setSentAt(Instant.now());
		return message;
	}
}