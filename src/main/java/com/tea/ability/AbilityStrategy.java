/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.ability;

import com.tea.model.Char;

/**
 *
 * @author Admin
 */
public interface AbilityStrategy {

    public abstract void setAbility(Char owner);
}
