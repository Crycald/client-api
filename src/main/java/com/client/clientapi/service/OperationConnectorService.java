package com.client.clientapi.service;

import com.client.clientapi.domain.*;
import com.client.clientapi.domain.logs.OperationConnectorLogs;
import com.client.clientapi.exception.clinic.ClinicNotFoundException;
import com.client.clientapi.exception.connector.OperationConnectorNotFoundException;
import com.client.clientapi.exception.customer.CustomerNotFoundException;
import com.client.clientapi.exception.operation.OperationNotFoundException;
import com.client.clientapi.mapper.OperationConnectorMapper;
import com.client.clientapi.repository.OperationRepository;
import com.client.clientapi.repository.ClinicRepository;
import com.client.clientapi.repository.CustomerRepository;
import com.client.clientapi.repository.OperationConnectorRepository;
import com.client.clientapi.service.logs.OperationConnectorLogsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OperationConnectorService {
    private OperationConnectorMapper mapper;
    private OperationConnectorRepository repository;
    private ClinicRepository clinicRepository;
    private CustomerRepository customerRepository;
    private OperationRepository operationRepository;
    private OperationConnectorLogsService operationConnectorLogsService;
    private Logger logger = LoggerFactory.getLogger(OperationConnectorService.class);

    @Autowired
    public OperationConnectorService(OperationConnectorMapper mapper, OperationConnectorRepository repository, ClinicRepository clinicRepository, CustomerRepository customerRepository, OperationRepository operationRepository, OperationConnectorLogsService operationConnectorLogsService) {
        this.mapper = mapper;
        this.repository = repository;
        this.clinicRepository = clinicRepository;
        this.customerRepository = customerRepository;
        this.operationRepository = operationRepository;
        this.operationConnectorLogsService = operationConnectorLogsService;
    }

    public List<OperationConnectorDto> getOperationConnectors() {
        return mapper.list(repository.findAll());
    }

    private ClinicNotFoundException clinicNotFound(Long id) {
        return new ClinicNotFoundException(id);
    }

    private CustomerNotFoundException customerNotFound(Long id) {
        return new CustomerNotFoundException(id);
    }

    private OperationNotFoundException operationNotFound(Long id) {
        return new OperationNotFoundException(id);
    }

    private OperationConnectorNotFoundException operationConnectorNotFound(Long id) {
        return new OperationConnectorNotFoundException(id);
    }

    public OperationConnectorDto getOperationConnectorById(final Long id) {
        Optional<OperationConnector> operationConnector = repository.findById(id);
        return mapper.mapToDto(operationConnector.orElseThrow(() -> operationConnectorNotFound(id)));
    }

    public OperationConnectorDto createOperationConnector(final OperationConnectorDto operationConnectorDto) {
        operationConnectorDto.setId(null);
        Clinic clinic = clinicRepository.findById(operationConnectorDto.getClinicId()).orElseThrow(() -> clinicNotFound(operationConnectorDto.getClinicId()));
        Customer customer = customerRepository.findById(operationConnectorDto.getCustomerId()).orElseThrow(() -> customerNotFound(operationConnectorDto.getCustomerId()));
        Operation operation = operationRepository.findById(operationConnectorDto.getOperationActId()).orElseThrow(() -> operationNotFound(operationConnectorDto.getOperationActId()));
        OperationConnector operationConnector = mapper.map(operationConnectorDto, clinic, customer, operation);
        mapper.mapToDto(repository.save(operationConnector));

        OperationConnectorLogs operationConnectorLogs = new OperationConnectorLogs();
        operationConnectorLogs.setOperationConnectorId(operationConnector);
        operationConnectorLogs.setClinicId(clinic);
        operationConnectorLogs.setCustomerId(customer);
        operationConnectorLogs.setOperationId(operation);
        operationConnectorLogs.setOperation("CREATE");
        operationConnectorLogsService.createOperationConnectorLogs(operationConnectorLogs);

        logger.info("OPERATION CONNECTOR CREATED - ID: " + operationConnector.getId());

        return mapper.mapToDto(operationConnector);
    }

    public void deleteOperationConnector(final Long id) {
        try {
            repository.deleteById(id);
            logger.info("OPERATION CONNECTOR DELETED - ID: " + id);
        } catch (Exception e) {
            logger.warn("NOT FOUND OPERATION CONNECTOR WITH ID: " + id);
            throw operationConnectorNotFound(id);
        }

    }

    public OperationConnectorDto updateOperationConnector(final OperationConnectorDto operationConnectorDto) {
        OperationConnector operationConnector = repository.findById(operationConnectorDto.getId()).orElseThrow(() -> operationConnectorNotFound(operationConnectorDto.getId()));
        Clinic clinic = clinicRepository.findById(operationConnectorDto.getClinicId()).orElseThrow(() -> clinicNotFound(operationConnectorDto.getClinicId()));
        Customer customer = customerRepository.findById(operationConnectorDto.getCustomerId()).orElseThrow(() -> customerNotFound(operationConnectorDto.getCustomerId()));
        Operation operation = operationRepository.findById(operationConnectorDto.getOperationActId()).orElseThrow(() -> operationNotFound(operationConnectorDto.getOperationActId()));

        OperationConnectorLogs operationConnectorLogs = new OperationConnectorLogs();
        operationConnectorLogs.setOperationConnectorId(operationConnector);
        operationConnectorLogs.setClinicId(clinic);
        operationConnectorLogs.setCustomerId(customer);
        operationConnectorLogs.setOperationId(operation);
        operationConnectorLogs.setOperation("UPDATE");
        operationConnectorLogsService.createOperationConnectorLogs(operationConnectorLogs);

        logger.info("OPERATION CONNECTOR UPDATED - ID: " +operationConnector.getId());

        return mapper.mapToDto(repository.save(mapper.map(operationConnectorDto, clinic, customer, operation)));
    }

    public List<OperationConnectorDto> getListsByCustomerId(Long id) {
        return mapper.list(repository.findAllByCustomerId_Id(id));
    }

    public List<OperationConnectorDto> getListsByClinicId(Long id) {
        return mapper.list(repository.findAllByClinicId_Id(id));
    }
}
