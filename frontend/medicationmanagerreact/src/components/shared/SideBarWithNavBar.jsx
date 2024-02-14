import {
    Box,
    Collapse,
    Drawer,
    DrawerContent,
    DrawerOverlay,
    Flex,
    Icon,
    IconButton,
    Image,
    Input,
    InputGroup,
    InputLeftElement, Spinner,
    Text,
    useColorModeValue,
    useDisclosure,
    Wrap, WrapItem
} from '@chakra-ui/react';

import {FiMenu, FiSearch} from 'react-icons/fi';
import {MdHome, MdKeyboardArrowRight, MdMedication} from 'react-icons/md'
import {CgAdd} from "react-icons/cg";
import {useEffect, useState} from "react";
import MedsCard from "../medications/MedsCard.jsx";
import DrawerForm from "./patient/DrawerForm.jsx";
import NavBarUserOptions from "../shared/patient/NavBarUserOptions.jsx"

const SideBarWithNavBar = ({medications, fetchMedications, patientId}) => {
    const sidebar = useDisclosure();
    const medicationList = useDisclosure();
    const color = useColorModeValue("gray.600", "gray.300");
    const [loading, setLoading] = useState(true);
    const [searchTerm, setSearchTerm] = useState("");
    const handleSearchChange = (event) => {
        const capitalize = (str) => {
            return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
        }

        const newSearchTerm = event.target.value;
        setSearchTerm(capitalize(newSearchTerm));
    };
    useEffect(() => {
        const timer = setTimeout(() => {
            setLoading(false);
        }, 1200);

        return () => clearTimeout(timer);
    }, []); // Empty dependency array to run the effect only once
    const LoadSpinner = () => {
        if (loading) {
            return (
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            )
        } else {
            return (
                <Text>No Medication Available. Add new medications</Text>
                )
        }
    };

    const NavItem = (props) => {
        const {icon, children, ...rest} = props;
        return (
            <Flex
                align="center"
                px="4"
                pl="4"
                py="3"
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
                role="group"
                fontWeight="semibold"
                transition=".15s ease"
                {...rest}
            >
                {icon && (
                    <Icon
                        mx="2"
                        boxSize="4"
                        _groupHover={{
                            color: color,
                        }}
                        as={icon}
                    />
                )}
                {children}
            </Flex>
        );
    };

    const SidebarContent = (props) => {
        return (
            <Box
                as="nav"
                pos="fixed"
                top="0"
                left="0"
                zIndex="sticky"
                h="full"
                pb="10"
                overflowX="hidden"
                overflowY="auto"
                bg="white"
                _dark={{
                    bg: "gray.800",
                }}
                border
                color="inherit"
                borderRightWidth="1px"
                w="60"
                {...props}
            >
                <Flex px="4" py="5" align="center">
                    <Text
                        fontSize="xl"
                        ml="2"
                        color="brand.500"
                        _dark={{
                            color: "white",
                        }}
                        fontWeight="semibold"
                    >
                        Medication Manager
                    </Text>
                    <Image
                        borderRadius='full'
                        boxSize='80px'
                        src='https://i.imgur.com/2a6BAUx.jpg'
                        alt='Stock Med image Pixaby'
                    />
                </Flex>
                <Flex
                    direction="column"
                    as="nav"
                    fontSize="sm"
                    color="gray.600"
                    aria-label="Main Navigation"
                >
                    <NavItem icon={MdHome}>Home</NavItem>
                    <NavItem icon={MdMedication} onClick={medicationList.onToggle}>
                        Medications
                        <Icon
                            as={MdKeyboardArrowRight}
                            ml="auto"
                            transform={medicationList.isOpen && "rotate(90deg)"}
                        />
                    </NavItem>
                    <Collapse in={medicationList.isOpen}>
                        {fetchMedications && medications.map((medication, index) => (
                            <NavItem key={index} pl="12" py="2">
                                {medication.brandName}
                            </NavItem>
                        ))}
                    </Collapse>
                    <NavItem icon={CgAdd}>
                        <DrawerForm medications={medications} fetchMedications={fetchMedications}
                                    patientId={patientId}/>
                    </NavItem>
                </Flex>
            </Box>
        );
    }

    return (
        <Box
            as="section"
            bg="gray.50"
            _dark={{
                bg: "gray.700",
            }}
            minH="100vh"
        >
            <SidebarContent
                display={{
                    base: "none",
                    md: "unset",
                }}
            />
            <Drawer
                isOpen={sidebar.isOpen}
                onClose={sidebar.onClose}
                placement="left"
            >
                <DrawerOverlay/>
                <DrawerContent>
                    <SidebarContent w="full" borderRight="none"/>
                </DrawerContent>
            </Drawer>
            <Box
                ml={{
                    base: 0,
                    md: 60,
                }}
                transition=".3s ease"
            >
                <Flex
                    as="header"
                    align="center"
                    justify="space-between"
                    w="full"
                    px="4"
                    bg="white"
                    _dark={{
                        bg: "gray.800",
                    }}
                    borderBottomWidth="1px"
                    color="inherit"
                    h="14"
                >
                    <IconButton
                        aria-label="Menu"
                        display={{
                            base: "inline-flex",
                            md: "none",
                        }}
                        onClick={sidebar.onOpen}
                        icon={<FiMenu/>}
                        size="sm"
                    />
                    <InputGroup
                        w="96"
                        display={{
                            base: "none",
                            md: "flex",
                        }}
                    >
                        <InputLeftElement color="gray.500">
                            <FiSearch/>
                        </InputLeftElement>
                        <Input name="medicationSearch" placeholder="Search for Medications..." onChange={handleSearchChange}/>
                    </InputGroup>
                    <NavBarUserOptions/>
                </Flex>
                <Box as="main" p="5">
                    { fetchMedications && medications.length > 0 ? (
                        <Wrap justify='left' spacing='20px'>
                            {searchTerm.length === 0 ? (
                                medications.map((medication, index) => (
                                    <WrapItem key={index}>
                                        <MedsCard {...medication}
                                                  fetchMedications={fetchMedications}
                                                  patientId={patientId}/>
                                    </WrapItem>
                                ))
                            ) : (
                                medications && medications.some(med => med.brandName.startsWith(searchTerm)) ? (
                                    medications
                                        .filter(filteredMed => filteredMed.brandName.startsWith(searchTerm))
                                        .map((filteredMedication, index) => (
                                            <WrapItem key={index}>
                                                <MedsCard {...filteredMedication} fetchMedications={fetchMedications}/>
                                            </WrapItem>
                                        ))
                                ) : (
                                    <Text>No Medication found</Text>
                                )
                            )}
                        </Wrap>
                    ) : (
                        <LoadSpinner />
                    )}
                </Box>
            </Box>
        </Box>
    );
};
export default SideBarWithNavBar;