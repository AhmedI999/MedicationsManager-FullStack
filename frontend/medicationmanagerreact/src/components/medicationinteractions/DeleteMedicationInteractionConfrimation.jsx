import {
    AlertDialog, AlertDialogBody,
    AlertDialogContent, AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Button,
    useDisclosure
} from "@chakra-ui/react";
import React from "react";
import {errorNotification, successNotification} from "../../services/Notifications.js";
import {deleteMedicationInteraction} from "../../services/client.js";
import { CloseIcon } from '@chakra-ui/icons';
import { Circle } from '@chakra-ui/react';


function DeleteMedicationInteractionConfirmation( { medicationId, name, refetchInteractions, patientId } ) {

    const {isOpen, onOpen, onClose} = useDisclosure();
    const cancelRef = React.useRef();

    const handleDeleteInteraction = () => {
        deleteMedicationInteraction(patientId, medicationId, name).then( () => successNotification(
            `Delete Interaction ${name}`, `${name} has been deleted successfully`
        )).catch(err => {
            errorNotification(`Delete Interaction ${name}`,
                `Couldn't delete ${name}. Error ${err.code}: ${err.response.data.message}`)
        }).finally( ()=> {
            onClose();
            refetchInteractions();
        })
    };

    return (
        <>

            <Circle
                size={4}
                bg="red"
                onClick={onOpen}
                ml={1}
                cursor="pointer"
                display="inline-flex"
                alignItems="center"

            >
                <CloseIcon color="white" />
            </Circle>
            <AlertDialog
                isOpen={isOpen}
                leastDestructiveRef={cancelRef}
                onClose={onClose}
            >
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                            Delete Interaction {name}
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            Are you sure? You can't undo this action afterwards.
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose}>
                                Cancel
                            </Button>
                            <Button colorScheme='red' onClick={handleDeleteInteraction} ml={3}>
                                Delete
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    )
}

export default DeleteMedicationInteractionConfirmation;