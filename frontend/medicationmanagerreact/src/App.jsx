import SideBarWithNavBar from "./components/shared/SideBarWithNavBar.jsx";
import useMedications from "./services/useMedications.jsx";

function App() {

    const { medications, fetchMedications} = useMedications(1);

    return (
        <SideBarWithNavBar fetchMedications={fetchMedications} medications={medications} />
    );
}

export default App;