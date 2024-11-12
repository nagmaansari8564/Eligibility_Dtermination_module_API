package in.ashokit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "DC_EDUCATION")
public class DcEducation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer eduId;
	private Long caseNum;
	private String highestQualifiaction;
	private Integer graduationYear;
	private String universityName;

}
