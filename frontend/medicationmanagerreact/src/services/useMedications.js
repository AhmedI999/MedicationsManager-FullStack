import {useEffect, useState} from "react";
import {getPatientMedications} from "./client.js";

const useMedications = (patientId) => {
    const [medications, setMedications] = useState([]);
    const [loading, setLoading] = useState(false);
    const fetchMedications = () => {
        setLoading(true);
        getPatientMedications(4).then(res => {
            setMedications(res.data)
        }).catch(err => {
            console.error(err);
        }).finally(() => setLoading(false));
    };

    useEffect(() => {
        fetchMedications();
    }, [loading, patientId])
    return { medications, loading, fetchMedications };
}
export default useMedications;