package fi.invian.codingassignment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String name;
	
	@Column(name = "created_at", nullable = false, updatable = false, insertable = false)
	private Instant createdAt;
	
	public User(Long id,String name ) {
		this.id = id;
		this.name = name;
	}
}

