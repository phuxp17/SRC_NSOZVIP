/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.option;

import com.tea.server.GameData;
import com.tea.skill.SkillOptionTemplate;

public class SkillOption {

    public SkillOption(int templateId, int param) {
        this.param = param;
        this.optionTemplate = GameData.getInstance().getOptionTemplates().get(templateId);
    }

    public int param;
    public SkillOptionTemplate optionTemplate;
}
