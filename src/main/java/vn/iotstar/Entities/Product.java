package vn.iotstar.Entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=150)
    private String name;

    private Double price;

    private String image; // ảnh upload từ máy

    @ManyToOne
    @JoinColumn(name="category_id")
    private Category category;

	public Object getCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setImage(String fileName) {
		// TODO Auto-generated method stub
		
	}
}

