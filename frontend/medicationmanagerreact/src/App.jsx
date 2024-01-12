import SideBarWithNavBar from "./components/shared/SideBarWithNavBar.jsx";
import useMedications from "./services/useMedications.jsx";
import {getPatientId} from "./services/usePatientId.js";

function App() {
    const { medications, loading, fetchMedications} = useMedications(getPatientId());

    return (

        <SideBarWithNavBar
            fetchMedications={fetchMedications}
            medications={medications}
            patientId={getPatientId()}
            loading={loading}
        />

    );
}

export default App;