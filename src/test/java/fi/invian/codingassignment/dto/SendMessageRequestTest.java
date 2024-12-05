package fi.invian.codingassignment.dto;

import fi.invian.codingassignment.entity.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SendMessageRequestTest {
	
	private Validator validator;
	
	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}
	
	@Test
	void validSendMessageRequest_shouldPassValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Valid Title");
		request.setBody("This is a valid message body.");
		request.setRecipientIds(List.of(2L, 3L));
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).isEmpty();
	}
	
	@Test
	void senderId_isNull_shouldFailValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(null);
		request.setTitle("Valid Title");
		request.setBody("Valid Body");
		request.setRecipientIds(List.of(2L, 3L));
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("senderId")
				&& v.getMessage().equals("Sender ID must not be empty."));
	}
	
	@Test
	void senderId_isNegative_shouldFailValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(-1L);
		request.setTitle("Valid Title");
		request.setBody("Valid Body");
		request.setRecipientIds(List.of(2L, 3L));
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("senderId")
				&& v.getMessage().equals("Sender ID must be a positive number."));
	}
	
	@Test
	void title_isNull_shouldFailValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle(null);
		request.setBody("Valid Body");
		request.setRecipientIds(List.of(2L, 3L));
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title")
				&& v.getMessage().equals("Title cannot be empty."));
	}
	
	@Test
	void title_exceedsMaxLength_shouldFailValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("A".repeat(256));
		request.setBody("Valid Body");
		request.setRecipientIds(List.of(2L, 3L));
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("title")
				&& v.getMessage().equals("Title cannot exceed 255 characters."));
	}
	
	@Test
	void body_isBlank_shouldFailValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Valid Title");
		request.setBody("   ");
		request.setRecipientIds(List.of(2L, 3L));
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("body")
				&& v.getMessage().equals("Body cannot be empty."));
	}
	
	@Test
	void recipientIds_isEmpty_shouldFailValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Valid Title");
		request.setBody("Valid Body");
		request.setRecipientIds(List.of());
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("recipientIds")
				&& v.getMessage().equals("Recipient IDs must not be empty."));
	}
	
	@Test
	void recipientIds_containsNegativeValue_shouldFailValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Valid Title");
		request.setBody("Valid Body");
		request.setRecipientIds(List.of(-1L, 3L));
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().startsWith("recipientIds[0]")
				&& v.getMessage().equals("Recipient IDs must be positive numbers."));
	}
	
	@Test
	void recipientIds_exceedsMaxSize_shouldFailValidation() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Valid Title");
		request.setBody("Valid Body");
		request.setRecipientIds(List.of(2L, 3L, 4L, 5L, 6L, 7L));
		
		Set<ConstraintViolation<SendMessageRequest>> violations = validator.validate(request);
		assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("recipientIds")
				&& v.getMessage().equals("A message can have a maximum of 5 recipients."));
	}
	
	@Test
	void toMessageEntity_shouldReturnValidMessageEntity() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Valid Title");
		request.setBody("Valid Body");
		request.setRecipientIds(List.of(2L, 3L));
		
		Message message = request.toMessageEntity();
		
		assertThat(message).isNotNull();
		assertThat(message.getTitle()).isEqualTo("Valid Title");
		assertThat(message.getBody()).isEqualTo("Valid Body");
		assertThat(message.getSentAt()).isNotNull();
	}
}