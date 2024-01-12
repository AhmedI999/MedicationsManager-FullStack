import {errorNotification} from "./Notifications.js";
import axios from "axios";
import {getCookie} from "./cookieUtils.js";
import {decryptData} from "./JwtEncryptionUtil.jsx";

const getToken = async () => {
    const secret = "@nEh;=uPkZt&Xvd(5№6'Ek>M~Rbv~%>j"
    const encryptedToken = getCookie("jwt");
    const jwt = await decryptData(encryptedToken, secret);
    if (!jwt)
        console.error("ErrorPage:", "Authentication Failed");
    return jwt
};

export const login = async (emailAndPassword) => {
    return await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/login`, emailAndPassword);
};

export const getPatientById = async (patientId) => {
    const token = await getToken();
    return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/id/${patientId}`,
        { headers: { Authorization: `Bearer ${token}` } }
    )

}

export const getPatientByEmail = async (email) => {
    const token = await getToken();
    return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${email}`,
        {headers: {Authorization: `Bearer ${token}`}}
    )
}
export const editPatient = async (patientId, patient) => {
    try {
        const token = await getToken();
        return await axios.put(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}`,
            patient,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch ( error ){
        errorNotification("Updating User", `Error Updating User details ${error.code}: ${error.response.data.message}`)
        throw error;
    }
}
export const editPatientPassword = async (patientId, Password) => {
    try {
        const token = await getToken();
        return await axios.put(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/change-password`,
            Password,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch ( error ){
        errorNotification("Changing Password", `Error Changing User Password ${error.code}: ${error.response.data.message}`)
        throw error;
    }
}


export const getPatientMedications= async (patientId) => {
    try {
        const token = await getToken();
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines`,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch ( error ){
        errorNotification("Getting patient medications", `Error fetching Medications Error ${error.code}: ${error.response.data.message}`)
        throw error;
    }

};
export const getPatientMedication = async (patientId, brandName) => {
    try {
        const token = await getToken();
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${brandName}`,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch ( error ){
        errorNotification("Getting patient medication", `Error fetching Medication ${brandName} Error ${error.code}: ${error.response.data.message}`)
        throw error;
    }

};
export const saveMedication = async (medicine, patientId) => {
    try {
        const token = await getToken();
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines`,
            medicine,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch (error) {
        errorNotification("Saving patient medication", `Error Saving Medication Error ${error.code}: ${error.response.data.message}`)
        throw error;
    }

};
export const editMedication = async (patientId, medicineId, medication) => {
    try {
        const token = await getToken();
        return await axios.put(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicineId}`,
            medication,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch ( error ){
        errorNotification("Updating patient medication", `Error updating Medication Error ${error.code}: ${error.response.data.message}`)
        throw error;
    }
}
export const deleteMedication = async (patientId, medicineId) => {
    try {
        const token = await getToken();
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicineId}`,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch ( error ){
        errorNotification("Deleting patient medication", `Error Deleting Medication Error ${error.code}: ${error.response.data.message}`)
        throw error;
    }
}

export const getMedicationInteractions = async (patientId, medicationId) => {
    try {
        const token = await getToken();
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicationId}/interactions`,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch ( error ){
        errorNotification("Getting Medication interactions", `Error Getting Medication interactions ${error.code}: ${error.response.data.message}`)
        throw error;
    }
}
export const saveMedicationInteraction = async (interaction, patientId, medicationId) => {
    try {
        const token = await getToken();
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicationId}/interactions`,
            interaction,
            { headers: { Authorization: `Bearer ${token}` } }
        )
    } catch ( error ){
        errorNotification("Saving Medication interactions", `Error Saving Medication interactions ${error.code}: ${error.response.data.message}`)
        throw error;
    }
}
export const deleteMedicationInteraction = async (patientId, medicationId, name) => {
    try {
        const token = await getToken();
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicationId}/interactions/${name}`,
            { headers: { Authorization: `Bearer ${token}` } }
            )
    } catch ( error ){
        errorNotification("Deleting Medication interactions", `Error Deleting Medication interactions ${error.code}: ${error.response.data.message}`)
        throw error;
    }
}