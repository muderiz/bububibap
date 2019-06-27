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

    ExtensionResult doTransferToAgent(ExtensionRequest extensionRequest);

    ExtensionResult doSendLocation(ExtensionRequest extensionRequest);

    ExtensionResult TipePencarian(ExtensionRequest extensionRequest);

    ExtensionResult doGetHospitalTerdekat(ExtensionRequest extensionRequest);

    ExtensionResult doCallHospital(ExtensionRequest extensionRequest);

    ExtensionResult doValidatePhone(ExtensionRequest extensionRequest);

    ExtensionResult doValidateDate(ExtensionRequest extensionRequest);

    ExtensionResult siloamMenggunakanBPJS(ExtensionRequest extensionRequest);

    ExtensionResult SetKonfirmasiTipe(ExtensionRequest extensionRequest);

    ExtensionResult setStepDua(ExtensionRequest extensionRequest);

    ExtensionResult setStepTiga(ExtensionRequest extensionRequest);

    ExtensionResult newGetDoctor(ExtensionRequest extensionRequest);

    ExtensionResult newGetScheduleDoctorId(ExtensionRequest extensionRequest);

    ExtensionResult newGetJamPraktek(ExtensionRequest extensionRequest);

    ExtensionResult tanyaNamaPasien(ExtensionRequest extensionRequest);

    ExtensionResult validasiNamaPasien(ExtensionRequest extensionRequest);

    ExtensionResult doPostCreateAppointment(ExtensionRequest extensionRequest);
}
