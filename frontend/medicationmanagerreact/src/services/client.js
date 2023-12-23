import axios from 'axios';

export const getPatientMedications= async (patientId) => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines`)
    } catch ( error ){
        console.error("Error fetching Medications:", error);
        throw error;
    }

};
export const getMedicationInteractions = async (patientId, medicationId) => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicationId}/interactions`)
    } catch ( error ){
    console.error("Error fetching Interactions:", error);
    throw error;
    }
}



export const saveMedication = async (medicine, patientId) => {
   try {
       return await axios.post(
           `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines`,
           medicine)
   } catch ( error ){
       console.error("Error fetching patients:", error);
       throw error;
   }

}
export const saveMedicationInteraction = async (interaction, patientId, medicationId) => {
    try {
        return await axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/${patientId}/medicines/${medicationId}/interactions`,
            interaction)
    } catch ( error ){
        console.error("Error fetching patients:", error);
        throw error;
    }
}