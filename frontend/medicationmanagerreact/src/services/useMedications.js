import {useEffect, useState} from "react";
import {getPatientMedications} from "./client.js";

const useMedications = (patientId) => {
    const [medications, setMedications] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        getPatientMedications(4).then(res => {
            setMedications(res.data)
        }).catch(err => {
            console.error(err);
        }).finally(() => setLoading(false));
    }, [loading, patientId])
    return {medications, loading};
}
export default useMedications;