package com.client.clientapi.mapper;

import com.client.clientapi.domain.*;
import com.client.clientapi.domain.enums.TypeOfOperation;
import com.client.clientapi.domain.enums.TypeOfAnimal;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class TypeOfOperationConnectorMapperTestSuite {
    private OperationConnectorMapper mapper = new OperationConnectorMapper();

    @Test
    public void testDtoToMap() {
        Clinic clinic1 = new Clinic();
        clinic1.setId(1L);
        clinic1.setName("comp_name");
        clinic1.setTypeOfAnimal(TypeOfAnimal.FRETKA);
        clinic1.setAddress("address");
        clinic1.setNip(12312312312L);
        clinic1.setPhoneNumber("123-123-123");
        clinic1.setMail("mail@mail.com");
        clinic1.setPassword("pwd123");

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setFirstname("Chris");
        customer1.setLastname("Mops");
        customer1.setPassword("123pwd");
        customer1.setEmail("mail@mail.com");
        customer1.setPhoneNumber("123-123-123");

        Operation operation1 = new Operation();

        operation1.setId(1L);
        operation1.setClinic_id(clinic1);
        operation1.setOperations(TypeOfOperation.AMPUTACJA);
        operation1.setCost(new BigDecimal(10000.00));

        OperationConnectorDto operationConnectorDto = new OperationConnectorDto(1L, 1L, 1L, 1L);
        OperationConnector operationConnector = mapper.map(operationConnectorDto, clinic1, customer1, operation1);

        Assert.assertEquals(operationConnector.getId(), operationConnectorDto.getId());
        Assert.assertEquals(operationConnector.getClinicId().getId(), operationConnectorDto.getClinicId());
        Assert.assertEquals(operationConnector.getCustomerId().getId(), operationConnectorDto.getCustomerId());
        Assert.assertEquals(operationConnector.getOperationId().getId(), operationConnectorDto.getOperationActId());
    }

    @Test
    public void testMapToDto() {
        Clinic clinic1 = new Clinic();
        clinic1.setId(1L);
        clinic1.setName("comp_name");
        clinic1.setTypeOfAnimal(TypeOfAnimal.FRETKA);
        clinic1.setAddress("address");
        clinic1.setNip(12312312312L);
        clinic1.setPhoneNumber("123-123-123");
        clinic1.setMail("mail@mail.com");
        clinic1.setPassword("pwd123");

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setFirstname("Chris");
        customer1.setLastname("Mops");
        customer1.setPassword("123pwd");
        customer1.setEmail("mail@mail.com");
        customer1.setPhoneNumber("123-123-123");

        Operation operation1 = new Operation();

        operation1.setId(1L);
        operation1.setClinic_id(clinic1);
        operation1.setOperations(TypeOfOperation.AMPUTACJA);
        operation1.setCost(new BigDecimal(10000.00));

        OperationConnector operationConnector = new OperationConnector(1L, clinic1, customer1, operation1, LocalDate.of(2020,10,10));
        OperationConnectorDto operationConnectorDto = mapper.mapToDto(operationConnector);

        Assert.assertEquals(operationConnectorDto.getId(), operationConnector.getId());
        Assert.assertEquals(operationConnectorDto.getClinicId(), operationConnector.getClinicId().getId());
        Assert.assertEquals(operationConnectorDto.getCustomerId(), operationConnector.getCustomerId().getId());
        Assert.assertEquals(operationConnectorDto.getOperationActId(), operationConnector.getOperationId().getId());
    }

    @Test
    public void testToList() {
        Clinic clinic1 = new Clinic();
        clinic1.setId(1L);
        clinic1.setName("comp_name");
        clinic1.setTypeOfAnimal(TypeOfAnimal.FRETKA);
        clinic1.setAddress("address");
        clinic1.setNip(12312312312L);
        clinic1.setPhoneNumber("123-123-123");
        clinic1.setMail("mail@mail.com");
        clinic1.setPassword("pwd123");

        Customer customer1 = new Customer();
        customer1.setId(1L);
        customer1.setFirstname("Chris");
        customer1.setLastname("Mops");
        customer1.setPassword("123pwd");
        customer1.setEmail("mail@mail.com");
        customer1.setPhoneNumber("123-123-123");

        Operation operation1 = new Operation();

        operation1.setId(1L);
        operation1.setClinic_id(clinic1);
        operation1.setOperations(TypeOfOperation.AMPUTACJA);
        operation1.setCost(new BigDecimal(10000.00));

        OperationConnector operationConnector1 = new OperationConnector(1L, clinic1, customer1, operation1, LocalDate.of(2020,10,10));

        List<OperationConnector> connectorList = new ArrayList<>();
        connectorList.add(operationConnector1);

        List<OperationConnectorDto> operationConnectorDtoList = mapper.list(connectorList);

        Assert.assertEquals(1, operationConnectorDtoList.size());
    }
}