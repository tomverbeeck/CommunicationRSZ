/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.validator;

/**
 *
 * @author Tom Verbeeck
 */
public class Validator {

    public boolean checkCheckinAtWorkNumber(String number) {
        boolean check = false;

        String newID = filterPointAndStripe(number);

        if (newID.length() > 13) {
            check = false;
        } else if (newID.length() <= 13) {
            check = true;
        }

        return check;
    }
    
    public boolean checkINSS(String inss){
        boolean check = false;
        
        String newINSS = filterPointAndStripe(inss);
        
        if(newINSS.length() > 11)
            check = false;
        else if(newINSS.length() <= 11)
            check = true;
        
        return check;
    }
    
    public boolean checkCompanyID(String id){
        boolean check = false;
        
        String newID = filterPointAndStripe(id);
        
        if(newID.length() > 13)
            check = false;
        else if(newID.length() <= 13)
            check = true;
        
        return check;
    }
    
    public boolean checkCancelReason(String reason){
        if(reason.equals("Holiday") || reason.equals("Disease") || reason.equals("Planning") || reason.equals("C32a") || reason.equals("Other"))
            return true;
        else
            return false;
    }

    private String filterPointAndStripe(String toFilter) {
        String returnString = "";
        if (toFilter.contains("-") || toFilter.contains(".")) {
            String[] partssInss = toFilter.split("-|\\.");
            for (String s : partssInss) {
                returnString += s;
            }
        } else {
            returnString = toFilter;
        }
        return returnString;
    }
}
