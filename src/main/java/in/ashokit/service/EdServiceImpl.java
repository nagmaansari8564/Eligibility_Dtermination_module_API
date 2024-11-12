package in.ashokit.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.ashokit.binding.EligResponse;
import in.ashokit.entity.CitizenAppEntity;
import in.ashokit.entity.CoTriggersEntity;
import in.ashokit.entity.DcCaseEntity;
import in.ashokit.entity.DcChildren;
import in.ashokit.entity.DcEducation;
import in.ashokit.entity.DcIncomeEntity;
import in.ashokit.entity.EligDtlsEntity;
import in.ashokit.entity.Plan;
import in.ashokit.repo.CitizenAppRepo;
import in.ashokit.repo.CoTriggersRepo;
import in.ashokit.repo.DcCaseRepo;
import in.ashokit.repo.DcChildrenRepo;
import in.ashokit.repo.DcEducationRepo;
import in.ashokit.repo.DcIncomeRepo;
import in.ashokit.repo.EligDtlsRepo;
import in.ashokit.repo.PlanRepo;

@Service
public class EdServiceImpl implements EdService {

	@Autowired
	private DcIncomeRepo incomeRepo;

	@Autowired
	private CitizenAppRepo citizenAppRepo;

	@Autowired
	private DcCaseRepo dcCaseRepo;

	@Autowired
	private PlanRepo planRepo;

	@Autowired
	private EligDtlsRepo eligDtlsRepo;

	@Autowired
	private DcChildrenRepo dcChildrenRepo;
	
	@Autowired
	private DcEducationRepo dcEducationRepo;
	
	@Autowired
	private CoTriggersRepo coTriggersRepo;

	@Override
	public EligResponse determinEligibility(Long caseNum) {

		Optional<DcCaseEntity> OpCaseEntity = dcCaseRepo.findById(caseNum);
		Integer appId = null;
		String planName = "";
		Integer planId = null;

		if (OpCaseEntity.isPresent()) {
			DcCaseEntity dcCaseEntity = OpCaseEntity.get();
			planId = dcCaseEntity.getPlanId();
			appId = dcCaseEntity.getAppId();

		}

		Optional<Plan> OpPlanEntity = planRepo.findById(planId);
		if (OpPlanEntity.isPresent()) {
			Plan plan = OpPlanEntity.get();
			planName = plan.getPlanName();

		}
		
		Optional<CitizenAppEntity> byId = citizenAppRepo.findById(appId);
		Integer age =0;
		CitizenAppEntity citizenAppEntity= null;
		if (byId.isPresent()) {
			citizenAppEntity = byId.get();
			LocalDate dob = citizenAppEntity.getDob();
			LocalDate now = LocalDate.now();
			age = Period.between(dob, now).getYears();
		}
		
        EligResponse response = exicutePlanCondition(caseNum, planName, age);
        
        EligDtlsEntity eligDtlsEntity = new EligDtlsEntity();
        BeanUtils.copyProperties(response, eligDtlsEntity);
        
        eligDtlsEntity.setCaseNum(caseNum);
        eligDtlsEntity.setHolderSsn(citizenAppEntity.getSsn());
        eligDtlsEntity.setHolderName(citizenAppEntity.getFullName());
        eligDtlsRepo.save(eligDtlsEntity);
        
        CoTriggersEntity triggerEntity = new CoTriggersEntity();
        triggerEntity.setCaseNum(caseNum);
        triggerEntity.setTrgStatus("Pending");
        coTriggersRepo.save(triggerEntity);
        
		return response;
	}

	private EligResponse exicutePlanCondition(Long caseNum, String planName, Integer age) {
		EligResponse response = new EligResponse();

		DcIncomeEntity incomeEntity = incomeRepo.findByCaseNum(caseNum);
//  SNAP
		if ("SNAP".equals(planName)) {

			Double empIncome = incomeEntity.getEmpIncome();
			if (empIncome <= 350) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DE");
				response.setDenialReasion("Heigh Income");
			}

		} else if ("CCAP".equals(planName)) {

			List<DcChildren> childrens = dcChildrenRepo.findByCaseNum(caseNum);
			boolean kidsCountCondition = false;
			boolean ageCondition = true;

			if (!childrens.isEmpty()) {
				kidsCountCondition = true;
				for (DcChildren dcChildren : childrens) {
					Integer childAge = dcChildren.getChildAge();
					if (childAge > 16) {
						ageCondition = false;
					}

				}
			}

			if (incomeEntity.getEmpIncome() <= 350 && kidsCountCondition && ageCondition) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DE");
				response.setDenialReasion("Not satisfied business rules");
			}

		} else if ("Medicaid".equals(planName)) {
			if (incomeEntity.getEmpIncome() < 350 && incomeEntity.getPropertyIncome() == 0) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DE");
				response.setDenialReasion("high income");
			}
		}

		else if ("Medicare".equals(planName)) {
			
				if (age >= 65) {
					response.setPlanStatus("AP");
				} else {
					response.setPlanStatus("DE");
					response.setDenialReasion("Age not matched");
				}
			

		} else if ("NJW".equals(planName)) {

			DcEducation educationEntity = dcEducationRepo.findByCaseNum(caseNum);
			Integer graduationYear = educationEntity.getGraduationYear();
			int currYear= LocalDate.now().getYear();
			
			if(incomeEntity.getEmpIncome()==0 && currYear> graduationYear) {
				response.setPlanStatus("AP");
			} else {
				response.setPlanStatus("DE");
				response.setDenialReasion("Rules not satisfied");
			}
			
		}
		
		if(response.getPlanStatus().equals("AP")) {
			response.setPlanStartDate(LocalDate.now());
			response.setPlanEndDate(LocalDate.now().plusMonths(6));
			response.setBenefiteAmt(400.00);
			}
		
		response.setPlanName(planName);

		return response;

	}


}
