import SideBarWithNavBar from "./components/shared/SideBarWithNavBar.jsx";
import {useEffect, useState} from "react";
import {getPatients} from "./services/client.js";
import {Spinner, Text, Wrap, WrapItem} from "@chakra-ui/react";
import MedsCard from "./components/MedsCard.jsx";
import DrawerForm from "./components/DrawerForm.jsx";
import MoreDetailsDrawer from "./components/MoreDetailsPopover.jsx";

function App() {
    const [medications, setMedications] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        setLoading(true);
        getPatients().then(res => {
            setMedications(res.data)
            console.log(...medications)
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
    if (medications.length <= 0) {
        return (
            <SideBarWithNavBar>
                <Text>No Patient available</Text>
            </SideBarWithNavBar>
        )
    }
    return (
        <SideBarWithNavBar>
            <Wrap justify="left" spacing="20px">
                {medications.map((medication, index) => (
                    <WrapItem key={index}>
                        <MedsCard
                            {...medication}
                        />
                    </WrapItem>
                ))}
            </Wrap>
        </SideBarWithNavBar>
    );
}

export default App
