package com.arnowouter.marcelleke.domain.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Arno Soontjens
 * This entity keeps track of the details of creation of other entities
 */
@Entity
@Table(name="CREATION_DETAILS")
public class creationDetailsEntity implements Serializable {

    private static final long serialVersionUID = 8L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    private Date creationDate;
    private Timestamp creationTime;
    
    public Long getId() {
        return id;
    }

    public Timestamp getCreationTime() {
    	return creationTime;
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
        if (!(object instanceof creationDetailsEntity)) {
            return false;
        }
        creationDetailsEntity other = (creationDetailsEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.arnowouter.entities.creationDetails[ id=" + id + " ]";
    }
    
}
