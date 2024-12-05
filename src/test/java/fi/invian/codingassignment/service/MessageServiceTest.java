package fi.invian.codingassignment.service;

import fi.invian.codingassignment.dto.MessageResponse;
import fi.invian.codingassignment.dto.SendMessageRequest;
import fi.invian.codingassignment.dto.StatisticsResponse;
import fi.invian.codingassignment.entity.Message;
import fi.invian.codingassignment.entity.User;
import fi.invian.codingassignment.exception.UserNotFoundException;
import fi.invian.codingassignment.repository.MessageRepository;
import fi.invian.codingassignment.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private MessageRepository messageRepository;
	
	@InjectMocks
	private MessageService messageService;
	
	@Test
	void testSendMessage_ValidRequest_ReturnsMessageId() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Hello");
		request.setBody("This is a message body");
		request.setRecipientIds(List.of(2L, 3L));
		
		User sender = new User(1L, "Sender");
		User recipient1 = new User(2L, "Recipient1");
		User recipient2 = new User(3L, "Recipient2");
		
		Message message = request.toMessageEntity();
		message.setId(10L);
		
		when(userRepository.findById(request.getSenderId())).thenReturn(Optional.of(sender));
		when(userRepository.findById(2L)).thenReturn(Optional.of(recipient1));
		when(userRepository.findById(3L)).thenReturn(Optional.of(recipient2));
		when(messageRepository.save(any(Message.class))).thenReturn(message);
		
		Long messageId = messageService.sendMessage(request);
		
		assertNotNull(messageId);
		assertEquals(10L, messageId);
		verify(userRepository, times(1)).findById(request.getSenderId());
		verify(userRepository, times(3)).findById(anyLong());
		verify(messageRepository, times(1)).save(any(Message.class));
	}
	
	@Test
	void testSendMessage_InvalidSender_ThrowsException() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(99L);
		request.setTitle("Hello");
		request.setBody("This is a message body");
		request.setRecipientIds(List.of(2L, 3L));
		
		when(userRepository.findById(request.getSenderId())).thenReturn(Optional.empty());
		
		UserNotFoundException exception = assertThrows(
				UserNotFoundException.class,
				() -> messageService.sendMessage(request)
		);
		
		assertEquals("Sender with ID 99 does not exist.", exception.getMessage());
	}
	
	@Test
	void testSendMessage_InvalidRecipient_ThrowsException() {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Hello");
		request.setBody("This is a message body");
		request.setRecipientIds(List.of(99L));
		
		User sender = new User(1L, "Sender");
		
		when(userRepository.findById(request.getSenderId())).thenReturn(Optional.of(sender));
		when(userRepository.findById(99L)).thenReturn(Optional.empty());
		
		UserNotFoundException exception = assertThrows(
				UserNotFoundException.class,
				() -> messageService.sendMessage(request)
		);
		
		assertEquals("Recipient with ID 99 does not exist.", exception.getMessage());
	}
	
	@Test
	void testGetUserMessages_ValidUser_ReturnsMessages() {
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 10);
		Page<MessageResponse> mockPage = new PageImpl<>(List.of(
				new MessageResponse(1L, "Hello", "Body", "Rishabh", Instant.now())
		));
		
		when(userRepository.existsById(userId)).thenReturn(true);
		when(messageRepository.findMessagesByRecipientId(userId, pageable)).thenReturn(mockPage);
		
		Page<MessageResponse> result = messageService.getUserMessages(userId, pageable);
		
		assertNotNull(result);
		assertEquals(1, result.getContent().size());
		assertEquals("Hello", result.getContent().get(0).title());
	}
	
	@Test
	void testGetUserMessages_UserNotFound_ThrowsException() {
		Long userId = 99L;
		Pageable pageable = PageRequest.of(0, 10);
		
		when(userRepository.existsById(userId)).thenReturn(false);
		
		UserNotFoundException exception = assertThrows(
				UserNotFoundException.class,
				() -> messageService.getUserMessages(userId, pageable)
		);
		
		assertEquals("User with ID 99 does not exist.", exception.getMessage());
	}
	
	@Test
	void testGetTopSenders_ReturnsSenders() {
		Instant fromDate = Instant.now().minus(Duration.ofDays(30));
		List<StatisticsResponse> mockResponse = List.of(
				new StatisticsResponse(1L, "Rishabh", 20L)
		);
		
		when(messageRepository.findTopSenders(fromDate, PageRequest.of(0, 10))).thenReturn(mockResponse);
		
		List<StatisticsResponse> result = messageService.getTopSenders(fromDate, 10);
		
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals("Rishabh", result.get(0).senderName());
		assertEquals(20, result.get(0).sentCount());
	}
}
