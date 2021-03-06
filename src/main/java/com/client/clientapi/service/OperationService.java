package com.client.clientapi.service;

import com.client.clientapi.domain.Clinic;
import com.client.clientapi.domain.Operation;
import com.client.clientapi.domain.OperationDto;
import com.client.clientapi.domain.logs.OperationLogs;
import com.client.clientapi.exception.clinic.ClinicNotFoundException;
import com.client.clientapi.exception.operation.OperationNotFoundException;
import com.client.clientapi.mapper.OperationMapper;
import com.client.clientapi.repository.OperationRepository;
import com.client.clientapi.repository.ClinicRepository;
import com.client.clientapi.service.logs.OperationLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperationService {
    private OperationRepository repository;
    private OperationMapper mapper;
    private ClinicRepository clinicRepository;
    private OperationLogsService operationLogsService;
    private Logger logger = LoggerFactory.getLogger(OperationService.class);

    @Autowired
    public OperationService(OperationRepository repository, OperationMapper mapper, ClinicRepository clinicRepository, OperationLogsService operationLogsService) {
        this.repository = repository;
        this.mapper = mapper;
        this.clinicRepository = clinicRepository;
        this.operationLogsService = operationLogsService;
    }

    public List<OperationDto> getOperations() {
        return mapper.list(repository.findAll());
    }

    private OperationNotFoundException operationNotFound(Long id) {
        return new OperationNotFoundException(id);
    }

    private ClinicNotFoundException clinicNotFound(Long id) {
        return new ClinicNotFoundException(id);
    }

    public OperationDto getOperationById(final Long id) {
        Optional<Operation> operations = repository.findById(id);
        return mapper.mapToDto(operations.orElseThrow(() -> operationNotFound(id)));
    }

    public OperationDto createOperation(final OperationDto operationDto) {
        operationDto.setId(null);
        Clinic clinic = clinicRepository.findById(operationDto.getClinicId()).orElseThrow(() -> clinicNotFound(operationDto.getClinicId()));
        Operation operation = mapper.map(operationDto, clinic);
        mapper.mapToDto(repository.save(operation));

        OperationLogs operationLogs = new OperationLogs();
        operationLogs.setOperationId(operation);
        operationLogs.setOperation("CREATE");
        operationLogsService.createOperationLogs(operationLogs);

        logger.info("OPERATION CREATED - ID: " + operation.getId());

        return mapper.mapToDto(operation);
    }

    public void deleteOperation(final Long id) {
        try {
            repository.deleteById(id);
            logger.info("OPERATION DELETED - ID: " + id);
        } catch (Exception e) {
            logger.warn("NOT FOUND OPERATION WITH ID: " + id);
            throw operationNotFound(id);
        }
    }

    public OperationDto updateOperation(final OperationDto operationDto) {
        Operation operation = repository.findById(operationDto.getId()).orElseThrow(() -> operationNotFound(operationDto.getId()));
        Clinic clinic = clinicRepository.findById(operationDto.getClinicId()).orElseThrow(() -> clinicNotFound(operationDto.getClinicId()));

        OperationLogs operationLogs = new OperationLogs();
        operationLogs.setOperationId(operation);
        operationLogs.setOperation("UPDATE");
        operationLogsService.createOperationLogs(operationLogs);

        logger.info("OPERATION UPDATED - ID: " + operation.getId());

        return mapper.mapToDto(repository.save(mapper.map(operationDto, clinic)));
    }

    public List<OperationDto> getOperationsByClinicId(Long id) {
        return mapper.list(repository.findAllByClinicId_Id(id));
    }
}