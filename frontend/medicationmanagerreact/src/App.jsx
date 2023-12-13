import SideBarWithNavBar from "./shared/SideBarWithNavBar.jsx";
import {useEffect, useState} from "react";
import {getPatients} from "./services/client.js";
import {Spinner, Text} from "@chakra-ui/react";

function App() {
    const [patients, setPatients] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        getPatients().then(res => {
            setPatients(res.data)
            console.log(res.data)
        }).catch(err => {
            console.error(err);
        }).finally(() => setLoading(false));
    }, [])
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
        )
    }
    if (patients.length <= 0) {
        return (
        <SideBarWithNavBar>
            <Text>No Patient available</Text>
        </SideBarWithNavBar>
        )
    }
    return (
        <div>
            <SideBarWithNavBar>
                {patients.map((patient, index) => (
                    <p key={index}>{patient.email}</p>
                ))}
            </SideBarWithNavBar>
        </div>
    );
}

export default App
