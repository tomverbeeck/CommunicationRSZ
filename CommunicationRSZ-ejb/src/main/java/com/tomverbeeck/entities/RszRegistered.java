/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Tom Verbeeck
 */
@Entity
@Table(name = "rsz_registered")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "RszRegistered.findAll", query = "SELECT r FROM RszRegistered r")
    , @NamedQuery(name = "RszRegistered.findById", query = "SELECT r FROM RszRegistered r WHERE r.id = :id")
    , @NamedQuery(name = "RszRegistered.findByPresenceRegistrationId", query = "SELECT r FROM RszRegistered r WHERE r.presenceRegistrationId = :presenceRegistrationId")
    , @NamedQuery(name = "RszRegistered.findByCreationDate", query = "SELECT r FROM RszRegistered r WHERE r.creationDate = :creationDate")
    , @NamedQuery(name = "RszRegistered.findByInss", query = "SELECT r FROM RszRegistered r WHERE r.inss = :inss")
    , @NamedQuery(name = "RszRegistered.findByStatus", query = "SELECT r FROM RszRegistered r WHERE r.status = :status")
    , @NamedQuery(name = "RszRegistered.findByCheckinAtWorkNumber", query = "SELECT r FROM RszRegistered r WHERE r.checkinAtWorkNumber = :checkinAtWorkNumber")})
public class RszRegistered implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "presenceRegistrationId")
    private String presenceRegistrationId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "creationDate")
    private String creationDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "inss")
    private String inss;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "status")
    private String status;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "checkinAtWorkNumber")
    private String checkinAtWorkNumber;

    public RszRegistered() {
    }

    public RszRegistered(Integer id) {
        this.id = id;
    }

    public RszRegistered(Integer id, String presenceRegistrationId, String creationDate, String inss, String status, String checkinAtWorkNumber) {
        this.id = id;
        this.presenceRegistrationId = presenceRegistrationId;
        this.creationDate = creationDate;
        this.inss = inss;
        this.status = status;
        this.checkinAtWorkNumber = checkinAtWorkNumber;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPresenceRegistrationId() {
        return presenceRegistrationId;
    }

    public void setPresenceRegistrationId(String presenceRegistrationId) {
        this.presenceRegistrationId = presenceRegistrationId;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getInss() {
        return inss;
    }

    public void setInss(String inss) {
        this.inss = inss;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckinAtWorkNumber() {
        return checkinAtWorkNumber;
    }

    public void setCheckinAtWorkNumber(String checkinAtWorkNumber) {
        this.checkinAtWorkNumber = checkinAtWorkNumber;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RszRegistered)) {
            return false;
        }
        RszRegistered other = (RszRegistered) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.tomverbeeck.entities.RszRegistered[ id=" + id + " ]";
    }
    
}
