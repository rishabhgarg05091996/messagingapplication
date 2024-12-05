INSERT INTO users (name, created_at) VALUES
                                         ('Rishabh', CURRENT_TIMESTAMP),
                                         ('Aish', CURRENT_TIMESTAMP),
                                         ('Ish', CURRENT_TIMESTAMP);

INSERT INTO messages (sender_id, title, body, sent_at) VALUES
                                                  (1, 'Hello, Aish!', 'This is a message to Aish.', CURRENT_TIMESTAMP),
                                                  (2, 'Meeting Reminder', 'Reminder about the meeting tomorrow.', CURRENT_TIMESTAMP);

INSERT INTO message_recipients (message_id, recipient_id) VALUES
                                                              (1, 2),
                                                              (2, 1),
                                                              (2, 3);