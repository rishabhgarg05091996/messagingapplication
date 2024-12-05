package fi.invian.codingassignment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.invian.codingassignment.dto.MessageResponse;
import fi.invian.codingassignment.dto.SendMessageRequest;
import fi.invian.codingassignment.dto.StatisticsResponse;
import fi.invian.codingassignment.exception.UserNotFoundException;
import fi.invian.codingassignment.service.MessageService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MessageControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private MessageService messageService;
	
	@Test
	void sendMessage_validRequest_shouldReturnSuccess() throws Exception {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Hello");
		request.setBody("Test body");
		request.setRecipientIds(List.of(2L, 3L));
		
		when(messageService.sendMessage(any(SendMessageRequest.class))).thenReturn(1L);
		
		mockMvc.perform(post("/messages")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(request)))
				.andExpect(status().isOk())
				.andExpect(content().string("Message sent successfully! ID: 1"));
	}
	
	@Test
	void sendMessage_invalidRequest_shouldReturnValidationErrors() throws Exception {
		SendMessageRequest invalidRequest = new SendMessageRequest();
		
		mockMvc.perform(post("/messages")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.senderId").value("Sender ID must not be empty."))
				.andExpect(jsonPath("$.title").value("Title cannot be empty."))
				.andExpect(jsonPath("$.body").value("Body cannot be empty."))
				.andExpect(jsonPath("$.recipientIds").value("Recipient IDs must not be empty."));
	}
	
	@Test
	void sendMessage_senderNotFound_shouldReturnNotFound() throws Exception {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(999L);
		request.setTitle("Hello");
		request.setBody("Test body");
		request.setRecipientIds(List.of(2L, 3L));
		
		when(messageService.sendMessage(any(SendMessageRequest.class)))
				.thenThrow(new UserNotFoundException("Sender with ID 999 does not exist."));
		
		mockMvc.perform(post("/messages")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(request)))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Sender with ID 999 does not exist."));
	}
	
	@Test
	void sendMessage_recipientNotFound_shouldReturnNotFound() throws Exception {
		SendMessageRequest request = new SendMessageRequest();
		request.setSenderId(1L);
		request.setTitle("Hello");
		request.setBody("Test body");
		request.setRecipientIds(List.of(2L, 999L));
		
		when(messageService.sendMessage(any(SendMessageRequest.class)))
				.thenThrow(new UserNotFoundException("Recipient with ID 999 does not exist."));
		
		mockMvc.perform(post("/messages")
						.contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(request)))
				.andExpect(status().isNotFound())
				.andExpect(content().string("Recipient with ID 999 does not exist."));
	}
	
	@Test
	void testGetMessages_ValidUser_ReturnsMessages() throws Exception {
		Long userId = 1L;
		Pageable pageable = PageRequest.of(0, 10, Sort.by("sentAt").descending());
		Page<MessageResponse> mockPage = new PageImpl<>(List.of(
				new MessageResponse(1L, "Hello", "Body", "Rishabh", Instant.now())
		));
		
		when(messageService.getUserMessages(userId, pageable)).thenReturn(mockPage);
		
		mockMvc.perform(get("/messages/users/{id}", userId)
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].id").value(1))
				.andExpect(jsonPath("$.content[0].title").value("Hello"));
	}
	
	@Test
	void testGetMessages_InvalidUser_ReturnsNotFound() throws Exception {
		Long userId = 99L;
		Pageable pageable = PageRequest.of(0, 10, Sort.by("sentAt").descending());
		
		when(messageService.getUserMessages(userId, pageable))
				.thenThrow(new UserNotFoundException("User with ID " + userId + " does not exist."));
		
		mockMvc.perform(get("/messages/users/{id}", userId))
				.andExpect(status().isNotFound())
				.andExpect(content().string("User with ID 99 does not exist."));
	}
	
	@Test
	void testInternalServerErrorInController() throws Exception {
		when(messageService.getUserMessages(anyLong(), any(Pageable.class)))
				.thenThrow(new RuntimeException("Simulated internal error"));
		
		mockMvc.perform(get("/messages/users/1")
						.param("page", "0")
						.param("size", "10"))
				.andExpect(status().isInternalServerError())
				.andExpect(content().string(containsString("An unexpected error occurred")));
	}
	
	@Test
	void testGetMessages_InvalidPageParams_ReturnsBadRequest() throws Exception {
		mockMvc.perform(get("/messages/users/1")
						.param("page", "-1")
						.param("size", "0"))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$", containsInAnyOrder(
						"Size must be at least 1.",
						"Page must be a positive number or zero."
				)));
	}
	
	@Test
	void testGetTopSenders_ValidRequest_ReturnsSenders() throws Exception {
		List<StatisticsResponse> mockResponse = List.of(
				new StatisticsResponse(1L, "Rishabh", 20L)
		);
		
		ArgumentCaptor<Instant> fromDateCaptor = ArgumentCaptor.forClass(Instant.class);
		
		when(messageService.getTopSenders(any(), eq(10))).thenReturn(mockResponse);
		
		mockMvc.perform(get("/messages/statistics/top-senders").param("limit", "10"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].senderId").value(1))
				.andExpect(jsonPath("$[0].senderName").value("Rishabh"))
				.andExpect(jsonPath("$[0].sentCount").value(20));
		
		verify(messageService).getTopSenders(fromDateCaptor.capture(), eq(10));
		
		Instant capturedFromDate = fromDateCaptor.getValue();
		Instant expectedFromDate = Instant.now().minus(Duration.ofDays(30));
		assertTrue(
				Duration.between(expectedFromDate, capturedFromDate).abs().toMillis() < 1000,
				"fromDate should be approximately 30 days before now"
		);
	}
	
	@Test
	void testGetTopSenders_InvalidLimit_ReturnsBadRequest() throws Exception {
		mockMvc.perform(get("/messages/statistics/top-senders").param("limit", "0"))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("[\"Size must be at least 1.\"]"));
	}
}
