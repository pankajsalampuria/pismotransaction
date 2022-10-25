package com.pismo.transaction.pismotransaction.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "OPERATION_TYPES")
public class OperationType extends AuditableEntity {

    @Id
    private Long id;
    @NotNull(message = "Operation Description must be present")
    private String description;

    public OperationType() {
        super();
    }

    public OperationType( Long id, String description) {
        this.id = id;
        this.description = description;
    }


    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OperationType that = (OperationType) o;
        return id.equals(that.id) && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "OperationType{" +
                "id=" + id +
                ", description='" + description + '\'' +
                '}';
    }
}
