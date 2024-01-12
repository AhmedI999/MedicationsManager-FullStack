import {useCallback, useEffect, useState} from "react";
import {getMedicationInteractions} from "./client.js";
import {errorNotification} from "./Notifications.js";
import {getPatientId} from "./usePatientId.js";

const useMedicationInteractions = (medicationId) => {
    const [interactions, setInteractions] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchInteractions = useCallback(async () => {
        setLoading(true);
        try {
            const res = await getMedicationInteractions(getPatientId(), medicationId);
            setInteractions(res.data);
        } catch (err) {
            errorNotification(`Getting Interactions`,
                `Couldn't Retrieve Interactions. Error ${err.code}: ${err.response.data.message}`)
        } finally {
            setLoading(false);
        }
    }, [medicationId]);

    useEffect(() => {
        fetchInteractions()
    }, [fetchInteractions]);

    const refetchInteractions = useCallback(() => {
        fetchInteractions()
    }, [fetchInteractions]);

    return { interactions, loading, refetchInteractions };
};

export default useMedicationInteractions;