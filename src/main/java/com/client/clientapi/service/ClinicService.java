package com.client.clientapi.service;

import com.client.clientapi.domain.Clinic;
import com.client.clientapi.domain.ClinicDto;
import com.client.clientapi.domain.enums.TypeOfAnimal;
import com.client.clientapi.domain.logs.ClinicLogs;
import com.client.clientapi.exception.clinic.ClinicNotFoundException;
import com.client.clientapi.mapper.ClinicMapper;
import com.client.clientapi.repository.ClinicRepository;
import com.client.clientapi.service.logs.ClinicLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClinicService {
    private ClinicRepository repository;
    private ClinicMapper mapper;
    private ClinicLogsService clinicLogsService;
    private Logger logger = LoggerFactory.getLogger(ClinicService.class);

    @Autowired
    public ClinicService(ClinicRepository repository, ClinicMapper mapper, ClinicLogsService clinicLogsService) {
        this.repository = repository;
        this.mapper = mapper;
        this.clinicLogsService = clinicLogsService;
    }

    public List<ClinicDto> getClinics() {
        return mapper.list(repository.findAll());
    }

    private ClinicNotFoundException clinicNotFound(Long id) {
        return new ClinicNotFoundException(id);
    }

    public ClinicDto getClinicById(final Long id) {
        Optional<Clinic> clinic = repository.findById(id);
        return mapper.mapToDto(clinic.orElseThrow(() -> clinicNotFound(id)));
    }

    public ClinicDto createClinic(final ClinicDto clinicDto) {
        clinicDto.setId(null);
        Clinic clinic = mapper.map(clinicDto);
        mapper.mapToDto(repository.save(clinic));

        ClinicLogs clinicLogs = new ClinicLogs();
        clinicLogs.setClinicId(clinic);
        clinicLogs.setOperation("CREATE");
        clinicLogs.setAddressNew(clinic.getAddress());
        clinicLogs.setNipNew(clinic.getNip());
        clinicLogs.setPhoneNumberNew(clinic.getPhoneNumber());
        clinicLogs.setMailNew(clinic.getMail());
        clinicLogs.setPasswordNew(clinic.getPassword());
        clinicLogsService.createClinicLogs(clinicLogs);

        logger.info("CLINIC CREATED - ID: " + clinic.getId());

        return mapper.mapToDto(clinic);
    }

    public void deleteClinic(final Long id) {
        try {
            repository.deleteById(id);
            logger.info("CLINIC DELETED - ID: " + id);
        } catch (Exception e) {
            logger.warn("NOT FOUND CLINIC WITH ID: " + id);
            throw clinicNotFound(id);
        }
    }

    public ClinicDto updateClinic(final ClinicDto clinicDto) {
        Clinic clinic = repository.findById(clinicDto.getId()).orElseThrow(() -> clinicNotFound(clinicDto.getId()));

        ClinicLogs clinicLogs = new ClinicLogs();
        clinicLogs.setClinicId(clinic);
        clinicLogs.setOperation("UPDATE");

        clinicLogs.setAddressOld(clinic.getAddress());
        clinicLogs.setNipOld(clinic.getNip());
        clinicLogs.setPhoneNumberOld(clinic.getPhoneNumber());
        clinicLogs.setMailOld(clinic.getMail());
        clinicLogs.setPasswordOld(clinic.getPassword());

        clinicLogs.setAddressNew(clinicDto.getAddress());
        clinicLogs.setNipNew(clinicDto.getNip());
        clinicLogs.setPhoneNumberNew(clinicDto.getPhoneNumber());
        clinicLogs.setMailNew(clinicDto.getMail());
        clinicLogs.setPasswordNew(clinicDto.getPassword());
        clinicLogsService.createClinicLogs(clinicLogs);

        logger.info("CLINIC UPDATED - ID: " + clinic.getId());

        return mapper.mapToDto(repository.save(mapper.map(clinicDto)));
    }

    public Long validateClinicAndReturnId(final String login, final String password) {
        Optional<Clinic> clinic = repository.findByLoginAndPassword(login, password);

        if (clinic.isPresent()) {
            logger.info("CLINIC HAS BEEN FOUND");
            System.out.println("Clinic ID: " + clinic.get().getId());
            return clinic.get().getId();
        } else {
            logger.warn("CLINIC NOT FOUND");
            return null;
        }
    }

    public List<ClinicDto> getClinicsByTypeOfAnimal(TypeOfAnimal type) {
        return mapper.list(repository.findAllByTypeOfAnimal(type));
    }

    public Boolean validateClinicLogin(final String login) {
        Optional<Clinic> clinic = repository.findByLogin(login);

        if (clinic.isPresent()) {
            logger.info("CLINIC WITH GIVEN LOGIN EXISTS");
            return true;
        } else {
            logger.warn("CLINIC NOT FOUND");
            return false;
        }
    }
}