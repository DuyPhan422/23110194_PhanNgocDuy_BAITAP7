package vn.iotstar.entity;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true)
	private String username;

	private String password;

	private String fullName;
	private String email;

	@Column(name = "phone_number")
	private String phone;

	@Enumerated(EnumType.STRING)
	private Role role;

	@OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    @JsonIgnore
	private Set<Category> categories;

	public enum Role {
		admin, manager, user
	}
}
