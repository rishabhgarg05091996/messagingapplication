package fi.invian.codingassignment.dto;

import java.time.Instant;

public record MessageResponse(
		Long id,
		String title,
		String body,
		String senderName,
		Instant sentAt
) {}