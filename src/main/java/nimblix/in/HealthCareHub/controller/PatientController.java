package nimblix.in.HealthCareHub.controller;

import jakarta.validation.Valid;
import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.response.ApiResponse;
import nimblix.in.HealthCareHub.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/register")
    public ApiResponse<Patient> registerPatient(@Valid @RequestBody PatientRegistrationRequest request) {

        // Call service to create Patient
        Patient savedPatient = patientService.registerPatient(request);

        // Return ApiResponse with patient data and message
        return new ApiResponse<>("SUCCESS", "Patient registered successfully", savedPatient);
    }
}