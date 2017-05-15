
package com.arnowouter.marcelleke.domain.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * @author Arno Soontjens
 * Keeps all information about companies in our database
 */
@Entity
@Table(name="COMPANY")
@NamedQueries({
    @NamedQuery(name = CompanyEntity.FIND_ALL, query = "SELECT c FROM CompanyEntity c"),
    @NamedQuery(name = CompanyEntity.FIND_BY_NAME, query = "SELECT c FROM CompanyEntity c WHERE c.name = :name" ),
    @NamedQuery(name = CompanyEntity.FIND_BY_ERP, query = "SELECT c FROM CompanyEntity c WHERE c.erpSystem = :erp"),
    @NamedQuery(name = CompanyEntity.FIND_BY_COMPANYCODE, query = "SELECT c FROM CompanyEntity c WHERE c.companyCode = :companyCode")
})
public class CompanyEntity implements Serializable {
    
    public static final String FIND_ALL = "CompanyEntity.findAll";
    public static final String FIND_BY_NAME = "CompanyEntity.findByName";
    public static final String FIND_BY_ERP = "CompanyEntity.findByERP";
    public static final String FIND_BY_COMPANYCODE = "CompanyEnitiy.findByCompanyCode";
    
    private static final long serialVersionUID = 2L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String companyCode;
    @ManyToMany
    private List<ContactInfoEntity> contactInfo;
    @ManyToMany
    private List<AddressEntity> addresses;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<ConnectionInfoEntity> connectionInformation;
    @ManyToOne
    private ERPSystemEntity erpSystem;
    @OneToMany
    private List<ModuleEntity> usedModules;
    @OneToMany
    private List<PreferenceEntity> preferences;

    public CompanyEntity() {
  
    }

    public CompanyEntity(String companyName) {
        this.name = companyName;
    }

    public CompanyEntity(
            String companyName, 
            String companyCode,
            List<ContactInfoEntity> companyContactInfo, 
            List<ConnectionInfoEntity> companyConnectionInfo,
            List<AddressEntity> companyAddresses, 
            ERPSystemEntity companyERPSystem, 
            List<ModuleEntity> companyUsedModules, 
            List<PreferenceEntity> companyPreferences
    ) {
        this.name = companyName;
        this.companyCode = companyCode;
        this.contactInfo = companyContactInfo;
        this.connectionInformation = companyConnectionInfo;
        this.addresses = companyAddresses;
        this.erpSystem = companyERPSystem;
        this.usedModules = companyUsedModules;
        this.preferences = companyPreferences;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String companyName) {
        this.name = companyName;
    }

    public List<ContactInfoEntity> getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(List<ContactInfoEntity> companyContactInfo) {
        this.contactInfo = companyContactInfo;
    }

    public List<AddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressEntity> addresses) {
        this.addresses = addresses;
    }
    
    public ERPSystemEntity getErpSystem() {
        return erpSystem;
    }

    public void setErpSystem(ERPSystemEntity erpSystem) {
        this.erpSystem = erpSystem;
    }

    public List<ModuleEntity> getUsedModules() {
        return usedModules;
    }

    public void setUsedModules(List<ModuleEntity> usedModules) {
        this.usedModules = usedModules;
    }

    public List<PreferenceEntity> getPreferences() {
        return preferences;
    }

    public void setPreferences(List<PreferenceEntity> preferences) {
        this.preferences = preferences;
    }

    public List<ConnectionInfoEntity> getConnectionInformation() {
        return connectionInformation;
    }

    public void setConnectionInformation(List<ConnectionInfoEntity> connectionInformation) {
        this.connectionInformation = connectionInformation;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public AddressEntity getCompanyPrimaryAddress() {
        AddressEntity primary = null;
        for(AddressEntity address : addresses){
            if (address.isIsPrimaryAddress()){
                primary = address;
            }
        }
        return primary;
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
        if (!(object instanceof CompanyEntity)) {
            return false;
        }
        CompanyEntity other = (CompanyEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CompanyEntity{" + 
                "id=" + id + 
                ", companyName=" + name + "\n" +
                ", companyContactInfo=" + contactInfo + "\n" +
                ", companyAddresses=" + addresses + "\n" +
                ", companyERPSystem=" + erpSystem + "\n" +
                ", companyUsedModules=" + usedModules + "\n" +
                ", companyPreferences=" + preferences +
                '}';
    }

    
    
}
