import {useEffect, useState} from "react";
import {getPatientMedications} from "./client.js";
import {errorNotification} from "./Notifications.js";

const useMedications = (patientId) => {
    const [medications, setMedications] = useState([]);
    const [loading, setLoading] = useState(false);
    const fetchMedications = () => {
        setLoading(true);
        getPatientMedications(4).then(res => {
            setMedications(res.data)
        }).catch(err => {
            errorNotification(`Getting Medications`,
                `Couldn't Retrieve Medications. Error ${err.code}: ${err.response.data.message}`)
        }).finally(() => setLoading(false));
    };


    useEffect(() => {
        fetchMedications();
    }, [loading, patientId])
    return { medications, loading, fetchMedications };

}
export default useMedications;