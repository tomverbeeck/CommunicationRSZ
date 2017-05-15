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
 * Keeps contact information about companies in our database
 */
@Entity
@Table(name="CONTACT_INFO")
@NamedQueries({
    @NamedQuery(name = ContactInfoEntity.FIND_ALL, query = "SELECT ci FROM ContactInfoEntity ci"),
    @NamedQuery(name = ContactInfoEntity.FIND_BY_NAME, query = "SELECT ci FROM ContactInfoEntity ci WHERE ci.name = :name"),
    @NamedQuery(name = ContactInfoEntity.FIND_BY_PHONENUMBER, query = "SELECT ci FROM ContactInfoEntity ci WHERE ci.phoneNumber = :phoneNumber"),
    @NamedQuery(name = ContactInfoEntity.FIND_BY_EMAIL, query = "SELECT ci FROM ContactInfoEntity ci WHERE ci.email = :email")
})
public class ContactInfoEntity implements Serializable {

    public static final String FIND_ALL = "ContactInfoEntity.findAll";
    public static final String FIND_BY_NAME = "ContactInfoEntity.findByName";
    public static final String FIND_BY_PHONENUMBER = "ContactInfoEntity.findByPhoneNumber";
    public static final String FIND_BY_EMAIL = "ContactInfoEntity.findByEmail";
    
    private static final long serialVersionUID = 4L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull
    private String name;
    private String phoneNumber;
    @NotNull
    private String email;

    public ContactInfoEntity() {
    }

    public ContactInfoEntity(String contactName, String telephoneNumber,String email) {
        this.name = contactName;
        this.phoneNumber = telephoneNumber;
        this.email = email;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        if (!(object instanceof ContactInfoEntity)) {
            return false;
        }
        ContactInfoEntity other = (ContactInfoEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ContactInfoEntity{" + "id=" + id + 
                ", contactName=" + name + 
                ", phoneNumber=" + phoneNumber + 
                ", email=" + email + '}';
    }
    
}
