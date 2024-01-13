import {
    AlertDialog, AlertDialogBody,
    AlertDialogContent, AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogOverlay,
    Button, Text,
    useDisclosure
} from "@chakra-ui/react";
import React from "react";
import {useNavigate} from "react-router-dom";
import {deletePatient} from "../../../services/client.js";
import {getPatientId} from "../../../services/patient/usePatientId.js";
import {errorNotification, successNotification} from "../../../services/Notifications.js";
import {deleteCookie} from "../../../services/jwt/cookieUtils.js";

function CloseAccount() {

    const {isOpen, onOpen, onClose} = useDisclosure();
    const cancelRef = React.useRef();
    const navigate = useNavigate();

    const handleDeleteAccount = () => {
        deletePatient(getPatientId())
            .then( () => {
                successNotification(
                    'Closing Account', 'Your account has been deleted successfully');
                deleteCookie("jwt");
                deleteCookie("i");
                navigate("/");
            }).catch(() => {
            errorNotification('Closing Account',
                `Error Deleting Account. Contact Admin`)
        });
    };

    return (
        <>
            <Text position="relative"
                  align="right"
                  mb={15}
                  cursor="pointer"
                  onClick={onOpen}
                  size="sm"
                  _hover={{ textDecoration: 'underline', color:'red' }}
            >
                Close The account
            </Text>

            <AlertDialog
                isOpen={isOpen}
                leastDestructiveRef={cancelRef}
                onClose={onClose}
            >
                <AlertDialogOverlay>
                    <AlertDialogContent>
                        <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                            Closing Your Account
                        </AlertDialogHeader>

                        <AlertDialogBody>
                            Are you sure? You can't undo this action afterwards.
                        </AlertDialogBody>

                        <AlertDialogFooter>
                            <Button ref={cancelRef} onClick={onClose}>
                                Cancel
                            </Button>
                            <Button colorScheme='red' onClick={handleDeleteAccount} ml={3}>
                                Delete
                            </Button>
                        </AlertDialogFooter>
                    </AlertDialogContent>
                </AlertDialogOverlay>
            </AlertDialog>
        </>
    )
}

export default CloseAccount;