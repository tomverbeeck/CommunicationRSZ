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
 * @author Arno Soontjens
 * Keeps information about how to connect to a certain ERP system.
 */
@Entity
@Table(name="CONNECTION_INFO")
@NamedQueries({
    @NamedQuery(name = ConnectionInfoEntity.FIND_ALL, query = "SELECT ci FROM ConnectionInfoEntity ci"),
    @NamedQuery(name = ConnectionInfoEntity.FIND_BY_HOSTNAME, query = "SELECT ci FROM ConnectionInfoEntity ci WHERE ci.hostName = :hostName")
})
public class ConnectionInfoEntity implements Serializable {

    public static final String FIND_ALL = "ConnectionInfoEntity.findAll";
    public static final String FIND_BY_HOSTNAME = "ConnectionInfoEntity.findByHostname";
    
    private static final long serialVersionUID = 3L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Size(min = 3)
    private String hostName;
    private String protocol;
    private int port;
    private String databaseName;
    private String databaseLogin;
    private String databasePassword;
    private boolean isPrimary;

    public ConnectionInfoEntity() {
    }

    public ConnectionInfoEntity(String hostName, String protocol, int port) {
        this.hostName = hostName;
        this.protocol = protocol;
        this.port = port;
    }

    public ConnectionInfoEntity(
            String hostName, 
            String protocol, 
            int port, 
            String databaseName, 
            String databaseLogin, 
            String databasePassword,
            boolean isPrimary
    ) {
        this.hostName = hostName;
        this.protocol = protocol;
        this.port = port;
        this.databaseName = databaseName;
        this.databaseLogin = databaseLogin;
        this.databasePassword = databasePassword;
        this.isPrimary = isPrimary;
    }

    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseLogin() {
        return databaseLogin;
    }

    public void setDatabaseLogin(String databaseLogin) {
        this.databaseLogin = databaseLogin;
    }

    public String getDatabasePassword() {
        return databasePassword;
    }

    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
        if (!(object instanceof ConnectionInfoEntity)) {
            return false;
        }
        ConnectionInfoEntity other = (ConnectionInfoEntity) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ConnectionInfoEntity{" + 
                "id=" + id + 
                ", hostName=" + hostName + 
                ", protocol=" + protocol + 
                ", port=" + port + 
                ", databaseName=" + databaseName + 
                ", databaseLogin=" + databaseLogin + 
                ", databasePassword=" + databasePassword + 
                '}';
    }

   
    
}
