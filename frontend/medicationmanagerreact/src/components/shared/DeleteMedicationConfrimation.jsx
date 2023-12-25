import {
    AlertDialog, AlertDialogBody,
    AlertDialogContent, AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Button,
    useDisclosure
} from "@chakra-ui/react";
import React from "react";
import {deleteMedication} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/Notifications.js";

function DeleteMedicationConfirmation({brandName, id, fetchMedications}) {

    const {isOpen, onOpen, onClose} = useDisclosure();
    const cancelRef = React.useRef();

    const handleDeleteMedication = () => {
        deleteMedication(4, id).then( () => successNotification(
            `Delete Medication ${brandName}`, `${brandName} has been deleted successfully`
        )).catch(err => {
            errorNotification(`Delete Medication ${brandName}`,
                `Couldn't delete ${brandName}. Error ${err.code}: ${err.response.data.message}`)
        }).finally( ()=> {
            onClose();
            fetchMedications();
        })
    };

    return (
        <>
            <Button
                colorScheme='red'
                onClick={onOpen}
                size="sm"
            >
                DEL
            </Button>

            <AlertDialog
                isOpen={isOpen}
                leastDestructiveRef={cancelRef}
                onClose={onClose}
            >
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                            Delete Medication {brandName}
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            Are you sure? You can't undo this action afterwards.
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose}>
                                Cancel
                            </Button>
                            <Button colorScheme='red' onClick={handleDeleteMedication} ml={3}>
                                Delete
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    )
}

export default DeleteMedicationConfirmation;