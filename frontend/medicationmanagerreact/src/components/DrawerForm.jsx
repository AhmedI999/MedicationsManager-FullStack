import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent, DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    Flex, useDisclosure
} from "@chakra-ui/react";
import AddMedicationForm from "./AddMedicationForm.jsx";


const DrawerForm = () =>{
    const { isOpen, onOpen, onClose } = useDisclosure()
    return <>
        <Drawer isOpen={isOpen} onClose={onClose} placement={"left"} size={"xl"}>
            <DrawerOverlay />
            <DrawerContent>
                <DrawerCloseButton />
                <DrawerHeader>Add a New Medication</DrawerHeader>
                <DrawerBody>
                    <AddMedicationForm/>
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
            color="inherit"
            _dark={{
                color: "gray.400",
            }}
            _hover={{
                    bg: "blue.100",
                    _dark: {
                        bg: "blue.900",
                    },
                    color: "blue.900",
                }}
            onClick={onOpen}
         >
                Add Medication
        </Flex>
    </>
}
export default DrawerForm;