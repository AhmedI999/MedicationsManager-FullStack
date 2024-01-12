import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react";
import EditMedicationForm from "./EditMedicationForm.jsx";

const EditMedicationDrawer = (
    {   pictureUrl,
        activeIngredient,
        brandName,
        id ,
        instructions,
        medicineNumber,
        timesDaily,
        fetchMedications,
        patientId} ) => {

    const { isOpen, onOpen, onClose } = useDisclosure()
    return <>
        <Drawer isOpen={isOpen} onClose={onClose} placement={"left"} size={"xl"}>
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Edit Medication {brandName} </DrawerHeader>
                <DrawerBody>
                    <EditMedicationForm
                        pictureUrl={pictureUrl}
                        activeIngredient={activeIngredient}
                        brandName={brandName}
                        id={id}
                        instructions={instructions}
                        medicineNumber={medicineNumber}
                        timesDaily={timesDaily}
                        fetchMedications={fetchMedications}
                        patientId={patientId}
                    />
                </DrawerBody>

                <DrawerFooter>
                    <Button onClick={onClose}>
                        Close
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
        <Button
            align="center"
            mx={2}
            size="sm"
            px="3"
            pl="3"
            py="4"
            cursor="pointer"
            color="inherit"
            colorScheme='green'
            borderWidth="2px"
            borderRadius="md"
            _dark={{
                color: "gray",
            }}
            _hover={{
                bg: "green.100",
                _dark: {
                    bg: "green.900",
                },
                color: "green.900",
                borderColor: "green.700",
            }}
            onClick={onOpen}
        >
            Edit
        </Button>
    </>
}
export default EditMedicationDrawer;