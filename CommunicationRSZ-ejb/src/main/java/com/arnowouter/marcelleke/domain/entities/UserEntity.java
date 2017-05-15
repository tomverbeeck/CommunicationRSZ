/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.domain.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author Arno
 */

@Entity
@Table(name="BADGERUSER")
@NamedQueries ({
    @NamedQuery(name = UserEntity.FIND_ALL, query = "SELECT u FROM UserEntity u"),
    @NamedQuery(name = UserEntity.FIND_BY_NAME, query = "SELECT u FROM UserEntity u WHERE u.name = :name"),
    @NamedQuery(name = UserEntity.FIND_ALL_LOGGED_IN, query = "SELECT u FROM UserEntity u WHERE u.loggedIn = true"),
    @NamedQuery(name = UserEntity.FIND_BY_CODE, query = "SELECT u FROM UserEntity u WHERE u.code = :code")
})
public class UserEntity implements Serializable {
    
    public static final String FIND_ALL = "UserEntity.findAll";
    public static final String FIND_BY_NAME = "UserEntity.findByName";
    public static final String FIND_ALL_LOGGED_IN = "UserEntity.findAllLoggedIn";
    public static final String FIND_BY_CODE = "UserEntity.findByCode";
    
    @Id @GeneratedValue 
    private Long id;
    @NotNull @Size(min=2)
    private String name;
    private String code;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    @ManyToOne
    private CompanyEntity company;
    private boolean loggedIn;

    public UserEntity() {
    }

    public UserEntity(String name, CompanyEntity company) {
        this.name = name;
        this.code = createUniqueCode();
        this.loggedIn = false;
        this.company = company;
        this.creationTime = new Date();
    }

    public Long getId() {return id;}
    public String getName() {return name;}
    public String getCode() {return code;}
    public boolean isLoggedIn() {return loggedIn;}
    public Date getCreationTime() {return creationTime;}
    public CompanyEntity getCompany() {return company;}
    
    public void setCompany(CompanyEntity company) {this.company = company;}
    public void setName(String name) {this.name = name;}
    public void setCode(String code) {this.code = code;}
    public void setLoggedIn(boolean loggedIn) {this.loggedIn = loggedIn;}

    @Override
    public String toString() {
        return "UserEntity{" + "id=" + id + ", name=" + name + ", code=" + code + ", loggedIn=" + loggedIn + '}';
    }
    
    private String createUniqueCode() {
        return UUID.randomUUID().toString().substring(0,5);
    }
}
