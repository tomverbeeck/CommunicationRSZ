/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.domain.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Arno Soontjens
 * Keeps information about the modules we have developed or are developing
 * to interface with certain ERP systems
 */
@Entity
@Table(name="MODULE")
public class ModuleEntity implements Serializable {

    private static final long serialVersionUID = 6L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String erpModuleName;
    private creationDetailsEntity erpModuleCreationDetails;
    @NotNull
    private boolean underDevelopment;

    public ModuleEntity() {
    }

    public ModuleEntity(String erpModuleName, boolean underDevelopment) {
        this.erpModuleName = erpModuleName;
        this.underDevelopment = underDevelopment;
    }

    public String getErpModuleName() {
        return erpModuleName;
    }

    public void setErpModuleName(String erpModuleName) {
        this.erpModuleName = erpModuleName;
    }

    public creationDetailsEntity getErpModuleCreationDetails() {
        return erpModuleCreationDetails;
    }

    public void setErpModuleCreationDetails(creationDetailsEntity erpModuleCreationDetails) {
        this.erpModuleCreationDetails = erpModuleCreationDetails;
    }

    public boolean isUnderDevelopment() {
        return underDevelopment;
    }

    public void setUnderDevelopment(boolean underDevelopment) {
        this.underDevelopment = underDevelopment;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof ModuleEntity)) {
            return false;
        }
        ModuleEntity other = (ModuleEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.arnowouter.entities.Modules[ id=" + id + " ]";
    }
    
}
