import {getCookie} from "./cookieUtils.js";
import {errorNotification} from "./Notifications.js";
import {getPatientById} from "./client.js";

export const getPatientId = () => {
    const patientId = getCookie('i');
    if (patientId === null) {
        errorNotification("Getting User Info", "Error Retrieving User info")
        return;
    }
    return patientId;
};
export const getPatient = async (patientId)=> {
    return await getPatientById(patientId)
}

