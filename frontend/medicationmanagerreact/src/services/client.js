import axios from 'axios';
import {errorNotification} from "./Notifications.js";

export const getPatientMedications= async (patientId) => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines`)
    } catch ( error ){
        errorNotification("Getting patient medications", `Error fetching Medications Error ${error.code}: ${error.response.data.message}`)
    }

};
export const getPatientMedication = async (patientId, brandName) => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${brandName}`)
    } catch ( error ){
        errorNotification("Getting patient medication", `Error fetching Medication ${brandName} Error ${error.code}: ${error.response.data.message}`)
    }

};
export const saveMedication = async (medicine, patientId) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines`,
            medicine)
    } catch (error) {
        errorNotification("Saving patient medication", `Error Saving Medication Error ${error.code}: ${error.response.data.message}`)
    }

};
export const editMedication = async (patientId, medicineId, medication) => {
    try {
        return await axios.put(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicineId}`,
            medication)
    } catch ( error ){
        errorNotification("Updating patient medication", `Error updating Medication Error ${error.code}: ${error.response.data.message}`)
    }
}
export const deleteMedication = async (patientId, medicineId) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicineId}`)
    } catch ( error ){
        errorNotification("Deleting patient medication", `Error Deleting Medication Error ${error.code}: ${error.response.data.message}`)
    }
}

export const getMedicationInteractions = async (patientId, medicationId) => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicationId}/interactions`)
    } catch ( error ){
        errorNotification("Getting Medication interactions", `Error Getting Medication interactions ${error.code}: ${error.response.data.message}`)
    }
}
export const saveMedicationInteraction = async (interaction, patientId, medicationId) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicationId}/interactions`,
            interaction)
    } catch ( error ){
        errorNotification("Saving Medication interactions", `Error Saving Medication interactions ${error.code}: ${error.response.data.message}`)
    }
}
export const deleteMedicationInteraction = async (patientId, medicationId, name) => {
    try {
        return await axios.delete(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicationId}/interactions/${name}`)
    } catch ( error ){
        errorNotification("Deleting Medication interactions", `Error Deleting Medication interactions ${error.code}: ${error.response.data.message}`)
    }
}