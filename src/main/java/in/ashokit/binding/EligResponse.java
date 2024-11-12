package in.ashokit.binding;

import java.time.LocalDate;

import lombok.Data;

@Data
public class EligResponse {

	
	public String planName;
	public String PlanStatus;
	public LocalDate planStartDate;
	public LocalDate planEndDate;
	public Double benefiteAmt;
	public String denialReasion;

}
