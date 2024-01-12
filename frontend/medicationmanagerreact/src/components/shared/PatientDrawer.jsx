import {
    Button, Divider,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay, Flex, Stack,
    useDisclosure
} from "@chakra-ui/react";
import EditPatientDetailsForm from "./EditPatientDetailsForm.jsx";
import ChangePasswordForm from "./ChangePasswordForm.jsx";


const PatientDrawer = ({refresh, patient, logout}) =>{
    const {isOpen, onOpen, onClose} = useDisclosure();

    const closeDrawer = () => onClose();
    return <>
        <Drawer isOpen={isOpen} onClose={onClose} placement={"bottom"} size={"full"} >
            <DrawerOverlay/>
            <DrawerContent width={"60%"}>
                <DrawerCloseButton/>
                <DrawerHeader>Edit your Details</DrawerHeader>
                <DrawerBody>
                    <EditPatientDetailsForm
                        close={closeDrawer}
                        patientData={patient}
                        refresh={refresh}
                        logout={logout}
                    />
                    <Stack mt={1} ml={0}>
                        <DrawerHeader>Change Password</DrawerHeader>
                        <Divider/>
                        <ChangePasswordForm id={patient.data.id} logout={logout}/>
                    </Stack>
                </DrawerBody>
                <DrawerFooter>
                    <Button onClick={onClose}>
                        Close
                    </Button>
                </DrawerFooter>
            </DrawerContent>
        </Drawer>
        <Flex
            align="center"
            px="4"
            pl="0"
            py="2"
            cursor="pointer"
            color="white"
            _dark={{
                color: "white.400",
            }}

            onClick={onOpen}
        >
            Account Details
        </Flex>
    </>;
}
export default PatientDrawer;