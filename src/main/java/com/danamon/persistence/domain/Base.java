package com.danamon.persistence.domain;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public abstract class Base implements Serializable {

    private static final long serialVersionUID = -7369920601847524273L;

    protected Base() { }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fgc_seq")
    @SequenceGenerator(name = "fgc_seq", sequenceName = "fgc_seq",
            allocationSize = 1,initialValue=27)
    protected Integer id;
    /**
     * @Id
     * @GeneratedValue
     * protected Integer id;*/

    /**
     * Random id for security reason
     */
    @Column(name = "SECURE_ID", unique = true, length = 36, nullable = false)
    private String secureId;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_CREATED", updatable = false, nullable = false)
    private Date creationDate;

    @CreatedBy
    @Column(name = "CREATED_BY", length = 30, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DATE_MODIFIED")
    private Date modificationDate;

    @LastModifiedBy
    @Column(name = "MODIFIED_BY", length = 30)
    private String modifiedBy;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version = 0;

    @PrePersist
    public void prePersist() {

        this.secureId = UUID.randomUUID().toString();
        this.creationDate = new Date();
    }

    @PreUpdate
    public void preUpdate(){
        this.modificationDate = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSecureId() {
        return secureId;
    }

    public void setSecureId(String secureId) {
        this.secureId = secureId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setCreateBaseDate(String by) {
        this.createdBy = by;
        this.creationDate = new Date();
        this.modifiedBy = by;
        this.modificationDate = new Date();
    }

    public void setUpdateBaseData(String by) {
        this.modifiedBy = by;
        this.modificationDate = new Date();
    }
}
