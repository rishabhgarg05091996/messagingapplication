package fi.invian.codingassignment.controller;

import fi.invian.codingassignment.dto.MessageResponse;
import fi.invian.codingassignment.dto.SendMessageRequest;
import fi.invian.codingassignment.dto.StatisticsResponse;
import fi.invian.codingassignment.service.MessageService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/messages")
@Validated
public class MessageController {
	private final MessageService messageService;
	
	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}
	
	@PostMapping
	public ResponseEntity<String> sendMessage(@Valid @RequestBody SendMessageRequest request) {
		Long messageId = messageService.sendMessage(request);
		return ResponseEntity.ok("Message sent successfully! ID: " + messageId);
	}
	
	@GetMapping("/users/{id}")
	public ResponseEntity<Page<MessageResponse>> getMessages(
			@PathVariable("id") Long userId,
			@RequestParam(defaultValue = "0") @PositiveOrZero(message = "Page must be a positive number or zero.") int page,
			@RequestParam(defaultValue = "10") @Positive(message = "Size must be at least 1.") int size) {
		
		Pageable pageable = PageRequest.of(page, size, Sort.by("sentAt").descending());
		Page<MessageResponse> messages = messageService.getUserMessages(userId, pageable);
		return ResponseEntity.ok(messages);
	}
	
	@GetMapping("/statistics/top-senders")
	public ResponseEntity<List<StatisticsResponse>> getTopSenders(
			@RequestParam(defaultValue = "10") @Positive(message = "Size must be at least 1.") int limit) {
		Instant fromDate = Instant.now().minus(Duration.ofDays(30));
		List<StatisticsResponse> stats = messageService.getTopSenders(fromDate, limit);
		return ResponseEntity.ok(stats);
	}
}