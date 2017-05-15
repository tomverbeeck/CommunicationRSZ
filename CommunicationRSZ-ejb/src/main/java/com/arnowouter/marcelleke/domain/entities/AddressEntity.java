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
import javax.validation.constraints.Size;

/**
 *
 * @author Arno Soontjens
 * Class that contains the structure for addresses
 */
@Entity
@Table(name = "ADDRESS")
@NamedQueries({
    @NamedQuery(name = AddressEntity.FIND_ALL, query = "SELECT a FROM AddressEntity a" ),
    @NamedQuery(name = AddressEntity.FIND_BY_STREET, query = "SELECT a FROM AddressEntity a WHERE a.street = :street"),
    @NamedQuery(name = AddressEntity.FIND_BY_CITY, query = "SELECT a FROM AddressEntity a WHERE a.city = :city" ),
    @NamedQuery(name = AddressEntity.FIND_BY_COUNTRY, query = "SELECT a FROM AddressEntity a WHERE a.country = :country" ),
    @NamedQuery(name = AddressEntity.FIND_BY_ZIPCODE, query = "SELECT a FROM AddressEntity a WHERE a.zipCode = :zipCode" ),
    @NamedQuery(name = AddressEntity.FIND_BY_PRIMARY, query = "SELECT a FROM AddressEntity a WHERE a.isPrimaryAddress = :primary" )
})
public class AddressEntity implements Serializable {

    public static final String FIND_ALL = "AddressEntity.findAll";
    public static final String FIND_BY_STREET = "AddressEntity.findByStreet";
    public static final String FIND_BY_CITY = "AddressEntity.findByCity";
    public static final String FIND_BY_COUNTRY = "AddressEntity.findByCountry";
    public static final String FIND_BY_ZIPCODE = "AddressEntity.findByZipcode";
    public static final String FIND_BY_PRIMARY = "AddressEntity.findByPrimaryAddress";
    
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Size(min = 2)
    private String country;
    @NotNull
    @Size(min = 2)
    private String city;
    private String zipCode;
    private String street;
    private boolean isPrimaryAddress;
    
    public AddressEntity() {
    }

    public AddressEntity(String Country, String City) {
        this.country = Country;
        this.city = City;
    }

    public AddressEntity(String Country, String City, String zipCode, String street) {
        this.country = Country;
        this.city = City;
        this.zipCode = zipCode;
        this.street = street;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isIsPrimaryAddress() {
        return isPrimaryAddress;
    }

    public void setIsPrimaryAddress(boolean isPrimaryAddress) {
        this.isPrimaryAddress = isPrimaryAddress;
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
        if (!(object instanceof AddressEntity)) {
            return false;
        }
        AddressEntity other = (AddressEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AddressEntity{" + 
                "id=" + id + 
                ", country=" + country + 
                ", city=" + city + 
                ", zipCode=" + zipCode + 
                ", street=" + street + 
                ", isPrimaryAddress=" + isPrimaryAddress + 
        '}';
    }

    
    
}
