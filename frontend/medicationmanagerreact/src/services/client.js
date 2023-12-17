import axios from 'axios';

// Getting Medicines for patient 1
export const getPatients= async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/1/medicines`)
    } catch ( error ){
        console.error("Error fetching patients:", error);
        throw error;
    }

};
export const saveMedicine = async (medicine) => {
   try {
       return await axios.post(
           `${import.meta.env.VITE_API_BASE_URL}/api/v1/patients/1/medicines`,
           medicine)
   } catch ( error ){
       console.error("Error fetching patients:", error);
       throw error;
   }

}
