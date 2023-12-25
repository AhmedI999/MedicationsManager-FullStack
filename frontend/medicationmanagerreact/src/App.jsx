import SideBarWithNavBar from "./components/shared/SideBarWithNavBar.jsx";
import {Spinner, Text} from "@chakra-ui/react";
import useMedications from "./services/useMedications.jsx";

function App() {

    const { medications, loading, fetchMedications} = useMedications(4);

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

    if (medications.length <= 0) {
        return (
            <SideBarWithNavBar>
                <Text>No Medications Here. Add New Medications</Text>
            </SideBarWithNavBar>
        );
    }

    return (
        <SideBarWithNavBar fetchMedications={fetchMedications} medications={medications} />
    );
}

export default App;