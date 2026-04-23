package org.bazar.bazarstorage.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * Сущность с общими полями для всех остальных Jpa сущностей
 */
@MappedSuperclass
@Getter
@Setter
public abstract class DomainObject {
    /**
     * Идентификатор сущности
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    /**
     * Дата и время создания
     */
    @Column(name = "created_at")
    @CreationTimestamp
    protected Instant createdAt;

    /**
     * Дата и время последнего обновления
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    protected Instant updatedAt;
}
