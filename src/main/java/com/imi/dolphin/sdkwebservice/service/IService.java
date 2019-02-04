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
package com.imi.dolphin.sdkwebservice.service;

import com.imi.dolphin.sdkwebservice.model.ExtensionRequest;
import com.imi.dolphin.sdkwebservice.model.ExtensionResult;

/**
 *
 * @author reja
 *
 */
public interface IService {

    ExtensionResult getSrnResult(ExtensionRequest extensionRequest);

    ExtensionResult getCustomerInfo(ExtensionRequest extensionRequest);

    ExtensionResult modifyCustomerName(ExtensionRequest extensionRequest);

    ExtensionResult getProductInfo(ExtensionRequest extensionRequest);

    ExtensionResult getMessageBody(ExtensionRequest extensionRequest);

    ExtensionResult getQuickReplies(ExtensionRequest extensionRequest);

    ExtensionResult getButtons(ExtensionRequest extensionRequest);

    ExtensionResult getCarousel(ExtensionRequest extensionRequest);

    ExtensionResult doTransferToAgent(ExtensionRequest extensionRequest);

    ExtensionResult doSendLocation(ExtensionRequest extensionRequest);

    ExtensionResult getSplitConversation(ExtensionRequest extensionRequest);

    ExtensionResult doGetAreas(ExtensionRequest extensionRequest);

    ExtensionResult doGetHospitalByArea(ExtensionRequest extensionRequest);

    ExtensionResult doGetDoctorByHospitalAndSpecialist(ExtensionRequest extensionRequest);

    ExtensionResult doGetDoctorBySpecialist(ExtensionRequest extensionRequest);

    ExtensionResult doGetDoctorSchedule(ExtensionRequest extensionRequest);

    ExtensionResult doGetDoctorByName(ExtensionRequest extensionRequest);

    ExtensionResult doGetScheduleByDoctorId(ExtensionRequest extensionRequest);

    ExtensionResult MenuDoctorSchedule(ExtensionRequest extensionRequest);

    ExtensionResult doGetHospitalTerdekat(ExtensionRequest extensionRequest);

    // get Specialist
    ExtensionResult doGetSpecialistbyHospital(ExtensionRequest extensionRequest);

    ExtensionResult doGetSpecialistbyName(ExtensionRequest extensionRequest);

    ExtensionResult doGetSpecialistPage2(ExtensionRequest extensionRequest);

    ExtensionResult doGetSpecialistPage3(ExtensionRequest extensionRequest);

    ExtensionResult doGetSpecialistPage4(ExtensionRequest extensionRequest);

    //
    ExtensionResult SetCounterSpecialist(ExtensionRequest extensionRequest);

    
    
    
    
    
    

    
}
