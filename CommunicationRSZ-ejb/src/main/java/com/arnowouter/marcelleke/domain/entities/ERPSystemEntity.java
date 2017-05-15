package com.arnowouter.marcelleke.domain.entities;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Arno Soontjens
 * Keeps information about ERP systems that we link to.
 */
@Entity
@Table(name="ERP_SYSTEM")
@NamedQueries( {
    @NamedQuery(name = ERPSystemEntity.FIND_ALL, query = "SELECT erp FROM ERPSystemEntity erp"),
    @NamedQuery(name = ERPSystemEntity.FIND_BY_NAME, query = "SELECT erp FROM ERPSystemEntity erp WHERE erp.erpName = :erpName")
})
public class ERPSystemEntity implements Serializable {

    public static final String FIND_ALL = "ERPSystemEntity.findAll";
    public static final String FIND_BY_NAME = "ERPSystemEntity.findByName";
    
    private static final long serialVersionUID = 5L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String erpName;
    private String erpWebsite;

    public ERPSystemEntity() {
    }

    public ERPSystemEntity(String erpName, String erpWebsite) {
        this.erpName = erpName;
        this.erpWebsite = erpWebsite;
    }

    public String getErpName() {
        return erpName;
    }

    public void setErpName(String erpName) {
        this.erpName = erpName;
    }

    public String getErpWebsite() {
        return erpWebsite;
    }

    public void setErpWebsite(String erpWebsite) {
        this.erpWebsite = erpWebsite;
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
        if (!(object instanceof ERPSystemEntity)) {
            return false;
        }
        ERPSystemEntity other = (ERPSystemEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ERPSystemEntity{" + "id=" + id + ", erpName=" + erpName + ", erpWebsite=" + erpWebsite +'}';
    }
}
