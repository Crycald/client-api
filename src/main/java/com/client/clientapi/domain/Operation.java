package com.client.clientapi.domain;

import com.client.clientapi.domain.enums.TypeOfOperation;
import com.client.clientapi.domain.logs.OperationConnectorLogs;
import com.client.clientapi.domain.logs.OperationLogs;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "OPERATION")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "CLINIC_ID")
    private Clinic clinicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "OPERATIONS", nullable = false)
    private TypeOfOperation operations;

    @Column(name = "COST")
    private BigDecimal cost;

    @OneToMany(
            targetEntity = OperationConnector.class,
            mappedBy = "operationId",
            cascade = CascadeType.REMOVE,
            fetch = FetchType.LAZY
    )
    private List<OperationConnector> list = new ArrayList<>();

    @OneToMany(
            mappedBy = "operationId",
            cascade = CascadeType.REMOVE
    )
    private List<OperationConnectorLogs> operationConnectorLogs = new ArrayList<>();

    @OneToMany(
            mappedBy = "operationId",
            cascade = CascadeType.REMOVE
    )
    private List<OperationLogs> operationLogs = new ArrayList<>();
}
