/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.beans;

import com.tomverbeeck.entities.RszRegistered;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
    
    
}
