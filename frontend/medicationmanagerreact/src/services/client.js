import axios from 'axios';

// for now we will get patients although we will get medications only
export const getPatients= async () => {
    try {
        return await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/patients`)
    } catch ( error ){
        console.error("Error fetching patients:", error);
        throw error;
    }

};