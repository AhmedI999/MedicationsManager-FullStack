import {
    Box,
    Flex,
    Avatar,
    Button,
    Menu,
    MenuButton,
    MenuList,
    MenuItem,
    MenuDivider,
    useColorModeValue,
    Stack,
    useColorMode,
    Center, Spinner,
} from '@chakra-ui/react';
import { MoonIcon, SunIcon } from '@chakra-ui/icons';
import PatientDrawer from "./PatientDrawer.jsx";
import usePatients from "../../services/usePatients.jsx";
import {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";
import {deleteCookie, getCookie} from "../../services/cookieUtils.js";
import {errorNotification, successNotification} from "../../services/Notifications.js";

export default function Nav() {
    const { colorMode, toggleColorMode } = useColorMode();
    const navigate = useNavigate()
    const bgColor = useColorModeValue('gray.100', 'gray.900');
    const { patientData } = usePatients();
    const [ patient, setPatient ]  = useState(null);
    useEffect(() => {
        setPatient(patientData);
    }, [patientData]);

    if ( ! patient ) {
        return (
            <Spinner
                thickness='4px'
                speed='0.65s'
                emptyColor='gray.200'
                color='blue.500'
                size='md'
            />
        );
    }

    const logout = () => {
        deleteCookie("jwt");
        deleteCookie("i");
        if (getCookie("jwt") === null){
            navigate("/")
            successNotification("Logout", "Logged out successfully")
        } else {
            errorNotification("logout error:", "Failed to logout properly.");
        }
    }
    const refresh = (patient) => {
        if ( patient !== patientData){
            setPatient(patient);
        }
    }

    const {firstname, lastname} = patient.data;

    return (
        <>
            <Box
                bg={bgColor}
                px={4}
                as="header"
                align="center"
                justify="space-between"
                _dark={{
                    bg: "gray.800",
                }}
                borderBottomWidth="1px"
                color="inherit"
                h="14"
            >
                <Flex h={16} alignItems={'center'} justifyContent={'space-between'}>
                    <Flex alignItems={'center'}>
                        <Stack direction={'row'} spacing={7}>
                            <Button onClick={toggleColorMode}>
                                {colorMode === 'light' ? <MoonIcon /> : <SunIcon />}
                            </Button>

                            <Menu>
                                <MenuButton
                                    as={Button}
                                    rounded={'full'}
                                    variant={'link'}
                                    cursor={'pointer'}
                                    minW={0}
                                >
                                    <Avatar
                                        size={'sm'}
                                        bgColor={'white'}
                                        src={'https://www.svgrepo.com/show/43426/profile.svg'}
                                    />
                                </MenuButton>
                                <MenuList alignItems={'center'}>
                                    <br />
                                    <Center>
                                        <Avatar
                                            size={'2xl'}
                                            bgColor={'white'}
                                            src={'https://www.svgrepo.com/show/43426/profile.svg'}
                                        />
                                    </Center>
                                    <br />
                                    <Center>
                                        <p>{firstname} {lastname}</p>
                                    </Center>
                                    <br />
                                    <MenuDivider />
                                    <MenuItem>
                                        <PatientDrawer refresh={refresh} patient={patient} logout={logout}/>
                                    </MenuItem>
                                    <MenuItem onClick={logout}>Logout</MenuItem>
                                </MenuList>
                            </Menu>
                        </Stack>
                    </Flex>
                </Flex>
            </Box>
        </>
    );
}
