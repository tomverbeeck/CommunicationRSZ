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
@Table(name = "rsz_pre_register")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Rsz.findAll", query = "SELECT r FROM Rsz r")
    , @NamedQuery(name = "Rsz.findById", query = "SELECT r FROM Rsz r WHERE r.id = :id")
    , @NamedQuery(name = "Rsz.findByInss", query = "SELECT r FROM Rsz r WHERE r.inss = :inss")
    , @NamedQuery(name = "Rsz.findByCompanyId", query = "SELECT r FROM Rsz r WHERE r.companyId = :companyId")
    , @NamedQuery(name = "Rsz.findByWorkPlaceId", query = "SELECT r FROM Rsz r WHERE r.workPlaceId = :workPlaceId")})
public class Rsz implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 45)
    @Column(name = "INSS")
    private String inss;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CompanyId")
    private String companyId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "WorkPlaceId")
    private String workPlaceId;

    public Rsz() {
    }

    public Rsz(Integer id) {
        this.id = id;
    }

    public Rsz(Integer id, String inss, String companyId, String workPlaceId) {
        this.id = id;
        this.inss = inss;
        this.companyId = companyId;
        this.workPlaceId = workPlaceId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInss() {
        return inss;
    }

    public void setInss(String inss) {
        this.inss = inss;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getWorkPlaceId() {
        return workPlaceId;
    }

    public void setWorkPlaceId(String workPlaceId) {
        this.workPlaceId = workPlaceId;
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
        if (!(object instanceof Rsz)) {
            return false;
        }
        Rsz other = (Rsz) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Rsz{" + "id=" + id + ", inss=" + inss + ", companyId=" + companyId + ", workPlaceId=" + workPlaceId + '}';
    }
}
