import {useState, useEffect, useCallback} from 'react';
import {getPatientId} from "./usePatientId.js";
import {getPatientById} from "./client.js";
import {errorNotification} from "./Notifications.js";
import {Spinner} from "@chakra-ui/react";

const usePatient = () => {
    const [loading, setLoading] = useState(true);
    const [patientData, setPatientData] = useState(null);

    const fetchPatientData = useCallback(() => {
        setLoading(true);
        getPatientById(getPatientId())
            .then((res) => {
                setPatientData(res);
                setLoading(false);
            })
            .catch((error) => {
                errorNotification('ErrorPage fetching patient data:', error);
                setLoading(false);
            });
    }, []);

    useEffect(() => {
        fetchPatientData();
    }, [fetchPatientData]);

    if (patientData === null) {
        return (
            <Spinner
                thickness='4px'
                speed='0.65s'
                emptyColor='gray.200'
                color='blue.500'
                size='md'
            />
        );
    }
    return { loading, patientData, fetchPatientData };
};

export default usePatient;