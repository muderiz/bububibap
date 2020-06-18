/**
 * Copyright (c) 2014 InMotion Innovation Technology. All Rights Reserved. <BR>
 * <BR>
 * This software contains confidential and proprietary information of InMotion
 * Innovation Technology. ("Confidential Information").<BR>
 * <BR>
 * Such Confidential Information shall not be disclosed and it shall only be
 * used in accordance with the terms of the license agreement entered into with
 * IMI; other than in accordance with the written permission of IMI. <BR>
 *
 *
 */
package com.imi.dolphin.sdkwebservice.builder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imi.dolphin.sdkwebservice.model.ButtonTemplate;

/**
 *
 * @author reja
 *
 */
public class ButtonBuilder {

    private ButtonTemplate buttonTemplateEntity;
    private static final String BUTTON_SYNTAX = "{button:";
    private static final String BUTTON_SYNTAX_SUFFIX = "}";

    /**
     *
     * @param buttonTemplateEntity Entitas ButtonBuilder
     */
    public ButtonBuilder(ButtonTemplate buttonTemplateEntity) {
        this.buttonTemplateEntity = buttonTemplateEntity;
    }

    /**
     *
     * @return button string Create Body JSON for Creating Button
     */
    public String build() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        String button = gson.toJson(getButtonTemplateEntity());
        button = BUTTON_SYNTAX + button + BUTTON_SYNTAX_SUFFIX;
        return button;
    }

    /**
     *
     * @return button template entity Untuk get Entitas Button Builder
     */
    public ButtonTemplate getButtonTemplateEntity() {
        return buttonTemplateEntity;
    }

    /**
     *
     * @param buttonTemplateEntity Entitas ButtonBuilder
     */
    public void setButtonTemplateEntity(ButtonTemplate buttonTemplateEntity) {
        this.buttonTemplateEntity = buttonTemplateEntity;
    }

}
