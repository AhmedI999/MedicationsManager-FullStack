import {useCallback, useEffect, useState} from "react";
import {getMedicationInteractions} from "./client.js";

const useMedicationInteractions = (medicationId) => {
    const [interactions, setInteractions] = useState([]);
    const [loading, setLoading] = useState(false);

    const fetchInteractions = useCallback(async () => {
        setLoading(true);
        try {
            const res = await getMedicationInteractions(4, medicationId);
            setInteractions(res.data);
        } catch (err) {
            console.error(err);
        } finally {
            setLoading(false);
        }
    }, [medicationId]);

    useEffect(() => {
        fetchInteractions().then((res) => console.log(res));
    }, [fetchInteractions]);

    const refetchInteractions = useCallback(() => {
        fetchInteractions().then((res) => console.log(res));
    }, [fetchInteractions]);

    return { interactions, loading, refetchInteractions };
};

export default useMedicationInteractions;