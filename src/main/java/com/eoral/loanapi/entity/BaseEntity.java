package com.eoral.loanapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BaseEntity {

    @Column
    @CreatedBy
    private String createdBy;

    @Column
    @CreatedDate
    private Instant createdDate;

    @Column
    @LastModifiedBy
    private String lastModifiedBy;

    @Column
    @LastModifiedDate
    private Instant lastModifiedDate;
}
