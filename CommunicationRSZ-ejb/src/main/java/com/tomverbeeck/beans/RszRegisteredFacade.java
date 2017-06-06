/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.beans;

import com.tomverbeeck.entities.RszRegistered;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

/**
 *
 * @author Tom Verbeeck
 */
@Stateless
@LocalBean
public class RszRegisteredFacade extends AbstractFacade<RszRegistered> {

    @PersistenceContext(unitName = PU)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RszRegisteredFacade() {
        super(RszRegistered.class);
    }
    
    public List<RszRegistered> getByCreationDate(String date) {
        TypedQuery<RszRegistered> query = em.createNamedQuery(RszRegistered.FIND_BY_CREATION_DATE, RszRegistered.class);
        query.setParameter("creationDate", date);
        return query.getResultList();
    }
    
    public List<RszRegistered> getByInss(String inss) {
        inss = inss.toLowerCase();
        TypedQuery<RszRegistered> query = em.createNamedQuery(RszRegistered.FIND_BY_INSS, RszRegistered.class);
        query.setParameter("inss", inss);
        return query.getResultList();
    }
    
}
