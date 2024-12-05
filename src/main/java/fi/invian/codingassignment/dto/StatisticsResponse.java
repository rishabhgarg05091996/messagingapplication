package fi.invian.codingassignment.dto;

public record StatisticsResponse(
		Long senderId,
		String senderName,
		Long sentCount
) {}