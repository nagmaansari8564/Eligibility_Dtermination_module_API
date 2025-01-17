package in.ashokit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "DC_CHILDREN")
public class DcChildren {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer childId;
	private String childName;
	private Long caseNum;
	private Integer childAge;
	private Long childSsn;
	

}
