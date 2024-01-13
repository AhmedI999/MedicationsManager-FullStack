import {useCallback, useEffect, useState} from "react";
import {Box, Spinner, Text} from "@chakra-ui/react";
import {getPatientMedications} from "../client.js";
import {errorNotification} from "../Notifications.js";
import SideBarWithNavBar from "../../components/shared/SideBarWithNavBar.jsx";

const useMedications = (patientId) => {
    const [medications, setMedications] = useState([]);
    const [loading, setLoading] = useState(false);
    const [err, setError] = useState("");
    const fetchMedications =  useCallback(() => {
        setLoading(true);
        getPatientMedications(patientId)
            .then((res) => {
                setMedications(res.data);
            })
            .catch((err) => {
                setError(err.response.data.message);
                errorNotification(
                    `Getting Medications`,
                    `Couldn't Retrieve Medications. Error ${err.code}: ${err.response.data.message}`
                );
            })
            .finally(() => {
                setLoading(false);
            });
    }, [patientId]);

    useEffect(() => {
        fetchMedications();
    }, [fetchMedications, patientId]);

    if (loading) {
        return (
            <SideBarWithNavBar>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SideBarWithNavBar>
        );
    }
    if (err) {
        return (
            <SideBarWithNavBar>
                <Box as="main" p="5">
                    <Text>Ooops there was an error</Text>
                </Box>
            </SideBarWithNavBar>
        )
    }

    return { medications, loading, fetchMedications };
};

export default useMedications;