/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tea.fashion;

import com.tea.constants.ItemName;
import com.tea.item.Equip;
import com.tea.item.Item;
import com.tea.item.Mount;
import com.tea.model.Char;
import com.tea.item.ItemTemplate;
import java.util.ArrayList;

/**
 *
 * @author Admin
 */
public class FashionFromEquip implements FashionStrategy {

    public void set(Char owner) {
        Equip eVuKhi = owner.equipment[ItemTemplate.TYPE_VUKHI];
        if (eVuKhi != null) {
            owner.weapon = eVuKhi.template.part;
        } else {
            owner.weapon = 15;
        }
        Equip eAo = owner.equipment[ItemTemplate.TYPE_AO];
        if (eAo != null) {
            owner.body = eAo.template.part;
        } else {
            if (owner.gender == 0) {
                owner.body = 10;
            } else {
                owner.body = 1;
            }
        }
        Equip eQuan = owner.equipment[ItemTemplate.TYPE_QUAN];
        if (eQuan != null) {
            owner.leg = eQuan.template.part;
        } else {
            if (owner.gender == 0) {
                owner.leg = 9;
            } else {
                owner.leg = 0;
            }
        }
        Equip eMatNa = owner.equipment[ItemTemplate.TYPE_MATNA];
        Item maskI = owner.getMask();
        if (eMatNa != null || maskI != null) {
            int id = -1;
            if (eMatNa != null) {
                owner.head = eMatNa.template.part;
                id = eMatNa.id;
            }
            if (maskI != null) {
                owner.head = maskI.template.part;
                id = maskI.id;
                if (id == ItemName.AKATSUKI_NAM  || id == ItemName.AKATSUKI_NU) {
                    owner.head = owner.original_head;
                }
            }
            switch (id) {
                case ItemName.THUY_TINH:
                    owner.body = 186;
                    owner.leg = 187;
                    break;

                case ItemName.SON_TINH:
                    owner.body = 189;
                    owner.leg = 190;
                    break;

                case ItemName.MAT_NA_THANH_GIONG_:
                    owner.body = 206;
                    owner.leg = 207;
                    break;

                case ItemName.MAT_NA_JIRAI_:
                    owner.body = 224;
                    owner.leg = 225;
                    break;

                case ItemName.MAT_NA_JUMITO:
                    owner.body = 227;
                    owner.leg = 228;
                    break;

                case ItemName.TOC_3:
                    owner.body = 230;
                    owner.leg = 231;
                    break;

                case ItemName.TOC_4:
                    owner.body = 233;
                    owner.leg = 234;
                    break;

                case ItemName.TOC_5:
                    owner.body = 236;
                    owner.leg = 237;
                    break;

                case ItemName.TOC_6:
                    owner.body = 239;
                    owner.leg = 240;
                    break;

                case ItemName.TOC_7:
                    owner.body = 242;
                    owner.leg = 243;
                    break;

                case ItemName.TOC_8:
                    owner.body = 245;
                    owner.leg = 246;
                    break;

                case ItemName.MAT_NA_CHUOT:
                    owner.body = 265;
                    owner.leg = 266;
                    break;

                case ItemName.JACK_HOLLOW:
                    owner.body = 259;
                    owner.leg = 260;
                    break;

                case ItemName.SANTA_CLAUS:
                    owner.body = 268;
                    owner.leg = 269;
                    break;

                case ItemName.SUMIMURA_:
                    owner.body = 271;
                    owner.leg = 272;
                    break;

                case ItemName.YUKIMURA_:
                    owner.body = 277;
                    owner.leg = 278;
                    break;

                case ItemName.TON_HANH_GIA:
                    owner.body = 202;
                    owner.leg = 203;
                    owner.weapon = 204;
                    break;
                case ItemName.AKATSUKI_NAM:
                    owner.ID_HAIR = 180;
                    owner.ID_BODY = 181;
                    owner.ID_LEG = 182;
                    break;
                case ItemName.AKATSUKI_NU:
                    owner.ID_HAIR = 183;
                    owner.ID_BODY = 184;
                    owner.ID_LEG = 185;
                    break;
                    
            }

        } else {
            owner.head = owner.original_head;
        }
        if (owner.fashion[11] != null) {
            owner.ID_MAT_NA = owner.fashion[11].template.fashion;
        } else {
            owner.ID_MAT_NA = -1;
        }
        if (owner.fashion[0] != null) {
            short idFashion = owner.fashion[0].template.fashion;
            if (idFashion > -1) {
                owner.ID_HAIR = idFashion;
                switch (idFashion) {
                    case 37:
                        owner.ID_BODY = 38;
                        owner.ID_LEG = 39;
                        break;

                    case 40:
                        owner.ID_BODY = 41;
                        owner.ID_LEG = 42;
                        break;

                    case 55:
                        owner.ID_BODY = 56;
                        owner.ID_LEG = 57;
                        break;

                    case 58:
                        owner.ID_BODY = 59;
                        owner.ID_LEG = 60;
                        break;

                    case 67:
                        owner.ID_BODY = 68;
                        owner.ID_LEG = 69;
                        break;

                    case 70:
                        owner.ID_BODY = 71;
                        owner.ID_LEG = 72;
                        break;

                    case 174:
                        owner.ID_BODY = 175;
                        owner.ID_LEG = 176;
                        break;

                    case 177:
                        owner.ID_BODY = 178;
                        owner.ID_LEG = 179;
                        break;
                     case 217:
                        owner.ID_BODY = 218;
                        owner.ID_LEG = 219;
                        break;

                    case 220:
                        owner.ID_BODY = 221;
                        owner.ID_LEG = 222;
                        break;
                        
                    case 129:
                        owner.ID_BODY = 130;
                        owner.ID_LEG = 131;
                        break;
                    case 135:
                        owner.ID_BODY = 136;
                        owner.ID_LEG = 137;
                        break;
                    case 180:
                        owner.ID_BODY = 181;
                        owner.ID_LEG = 182;
                        break;
                    case 183:
                        owner.ID_BODY = 184;
                        owner.ID_LEG = 185;
                        break;
                }
            }
        } else if (maskI == null
                || (maskI.id != ItemName.AKATSUKI_NAM && maskI.id != ItemName.AKATSUKI_NU)) {
            owner.ID_HAIR = -1;
            owner.ID_BODY = -1;
            owner.ID_LEG = -1;
        }
        
         if (owner.fashion[13] != null) {
            short idFashion = owner.fashion[13].template.fashion;
            if (idFashion > -1) {
                owner.ID_RANK = idFashion;
                switch (idFashion) {
                    case 78:
                        owner.ID_RANK = 78;
                        break;
                }
            }
        } else {
            owner.ID_RANK = -1;
            
        }
        Equip eAoChoang = owner.equipment[ItemTemplate.TYPE_AOCHOANG];
        if (eAoChoang != null) {
            short idFashion = eAoChoang.template.fashion;
            if (idFashion > -1) {
                owner.ID_PP = idFashion;
                owner.coat = -1;
            } else {
                owner.coat = (short) eAoChoang.id;
                owner.ID_PP = -1;
            }
        } else {
            owner.coat = -1;
            owner.ID_PP = -1;
        }
        if (owner.fashion[ItemTemplate.TYPE_THUNUOI] != null && owner.fashion[ItemTemplate.TYPE_THUNUOI].id != 864) {
            owner.ID_NAME = owner.fashion[ItemTemplate.TYPE_THUNUOI].template.fashion;
        } else {
            owner.ID_NAME = -1;
        }
        Equip eBaoTay = owner.equipment[ItemTemplate.TYPE_BAOTAY];
        if (eBaoTay != null) {
            owner.glove = (short) eBaoTay.id;
        } else {
            owner.glove = -1;
        }
        if (owner.fashion[1] != null) {
            short idFashion = owner.fashion[1].template.fashion;
            if (idFashion > -1) {
                owner.ID_WEA_PONE = idFashion;
            }
        } else {
            owner.ID_WEA_PONE = -1;
        }
        Mount mount = owner.mount[4];
        if (mount != null) {
            short idFashion = mount.template.fashion;
            if (idFashion > -1) {
                owner.ID_HORSE = idFashion;
            }
        } else {
            owner.ID_HORSE = -1;
        }
        ArrayList<Integer> listMax = new ArrayList<>();
        for (Equip equip : owner.equipment) {
            if (equip != null && (equip.template.isTypeClothe() || equip.template.isTypeAdorn()
                    || equip.template.isTypeWeapon())) {
                listMax.add(equip.getMaxUpgradeGem());
            }
        }
        if (listMax.size() == 10) {
            listMax.sort((o1, o2) -> o1 - o2);
            int min = listMax.get(0);
            switch (min) {
                case 6:
                case 7:
                    owner.haoQuang = 0;
                    break;
                case 8:
                case 9:
                    owner.haoQuang = 1;
                    break;
                case 10:
                    owner.haoQuang = 2;
                    break;
            }
        } else {
            owner.haoQuang = -1;
        }
        Equip eThuNuoi2 = owner.fashion[ItemTemplate.TYPE_THUNUOI];
        if (eThuNuoi2 != null && eThuNuoi2.id == 864) {
            if (owner.classId > 0) {
                short[] honorIds = new short[] { 16, 17, 15, 13, 12, 14 };
                owner.honor = honorIds[owner.classId - 1];
            }
        } else {
            owner.honor = -1;
        }
        if (owner.equipment[1] != null) {
            switch(owner.equipment[1].template.id) {
                case 1254: 
                    owner.ID_WEA_PONE = 170;
                    break;
                case 1255 : 
                    owner.ID_WEA_PONE = 173;
                    break;
                case 1256 : 
                    owner.ID_WEA_PONE = 172;
                    break;
                case 1257 : 
                    owner.ID_WEA_PONE = 169;
                    break;
                case 1258 : 
                    owner.ID_WEA_PONE = 168;
                    break;
                case 1259 : 
                    owner.ID_WEA_PONE = 171;  
                    break;
            }
        }
        if (owner.fashion[0] != null) {
            switch(owner.fashion[0].template.id) {
                case 1281: 
                    Item item1 = owner.fashion[0];
                    if(owner.gender==1){
                    owner.ID_WEA_PONE = 126;
                    owner.ID_BODY = 127;
                    owner.ID_LEG = 128;}
                    if(owner.gender==0){
                    owner.ID_WEA_PONE = 138;
                    owner.ID_BODY = 139;
                    owner.ID_LEG = 140;}
                    
                    break;
               
            }
        }
        
    }

}
