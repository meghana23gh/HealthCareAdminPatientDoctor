package nimblix.in.HealthCareHub.serviceImpl;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import nimblix.in.HealthCareHub.model.Hospital;
import nimblix.in.HealthCareHub.model.Patient;
import nimblix.in.HealthCareHub.model.Role;
import nimblix.in.HealthCareHub.model.User;
import nimblix.in.HealthCareHub.repository.HospitalRepository;
import nimblix.in.HealthCareHub.repository.UserRepository;
import nimblix.in.HealthCareHub.request.PatientRegistrationRequest;
import nimblix.in.HealthCareHub.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PatientServiceImpl implements PatientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Patient registerPatient(PatientRegistrationRequest request) {

        // 1. Check email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // 2. Check password match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }

        // 3. Create User
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.PATIENT);
        user.setEnabled(true);
        userRepository.save(user);

        // 4. Create Patient entity
        Patient patient = Patient.builder()
                .name(request.getFirstName() + " " + request.getLastName())
                .age(request.getAge())
                .gender(request.getGender())
                .phone(request.getPhone())
                .disease(request.getDisease())
                .admissionDate(request.getAdmissionDate() != null ? LocalDate.parse(request.getAdmissionDate()) : null)
                .dischargeDate(request.getDischargeDate() != null ? LocalDate.parse(request.getDischargeDate()) : null)
                .surgeryRequired(request.getSurgeryRequired())
                .emergencyCase(request.getEmergencyCase())
                .user(user)
                .hospital(request.getHospitalId() != null
                        ? hospitalRepository.findById(request.getHospitalId())
                        .orElseThrow(() -> new RuntimeException("Hospital not found"))
                        : null)
                .build();

        entityManager.persist(patient);

        return patient;
    }
}