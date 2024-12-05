package fi.invian.codingassignment.service;

import fi.invian.codingassignment.dto.MessageResponse;
import fi.invian.codingassignment.dto.SendMessageRequest;
import fi.invian.codingassignment.dto.StatisticsResponse;
import fi.invian.codingassignment.entity.Message;
import fi.invian.codingassignment.entity.User;
import fi.invian.codingassignment.exception.UserNotFoundException;
import fi.invian.codingassignment.repository.MessageRepository;
import fi.invian.codingassignment.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {
	
	private final MessageRepository messageRepository;
	private final UserRepository userRepository;
	
	public MessageService(MessageRepository messageRepository, UserRepository userRepository) {
		this.messageRepository = messageRepository;
		this.userRepository = userRepository;
	}
	
	@Transactional
	public Long sendMessage(SendMessageRequest request) {
		// Validate sender
		User sender = userRepository.findById(request.getSenderId())
				.orElseThrow(() -> new UserNotFoundException("Sender with ID " + request.getSenderId() + " does not exist."));
		
		// Convert request to entity
		Message message = request.toMessageEntity();
		message.setSender(sender);
		
		// Fetch recipients and add to message
		List<User> recipients = getRecipientsByIds(request.getRecipientIds());
		recipients.forEach(message::addRecipient);
		
		// Save the message
		return messageRepository.save(message).getId();
	}
	
	private List<User> getRecipientsByIds(List<Long> recipientIds) {
		return recipientIds.stream()
				.map(id -> userRepository.findById(id)
						.orElseThrow(() -> new UserNotFoundException("Recipient with ID " + id + " does not exist.")))
				.collect(Collectors.toList());
	}
	
	public Page<MessageResponse> getUserMessages(Long userId, Pageable pageable) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException("User with ID " + userId + " does not exist.");
		}
		return messageRepository.findMessagesByRecipientId(userId, pageable);
	}
	
	public List<StatisticsResponse> getTopSenders(Instant fromDate, int limit) {
		Pageable pageable = PageRequest.of(0, limit);
		return messageRepository.findTopSenders(fromDate, pageable);
	}
}