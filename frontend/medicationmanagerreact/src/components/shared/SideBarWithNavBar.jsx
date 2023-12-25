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
    InputLeftElement,
    Text,
    useColorModeValue,
    useDisclosure,
    Wrap, WrapItem
} from '@chakra-ui/react';

import {FiMenu, FiSearch} from 'react-icons/fi';
import {FaBell} from 'react-icons/fa'
import {MdHome, MdKeyboardArrowRight, MdMedication} from 'react-icons/md'
import {BsGearFill} from 'react-icons/bs'
import DrawerForm from "../DrawerForm.jsx";
import {CgAdd} from "react-icons/cg";
import {useState} from "react";
import MedsCard from "../MedsCard.jsx";


const SideBarWithNavBar = ({fetchMedications, medications}) => {
    const sidebar = useDisclosure();
    const integrations = useDisclosure();
    const color = useColorModeValue("gray.600", "gray.300");
    const [bellColor, setBellColor] = useState('grey')
    const handleBellClick = () => {
        // Toggle between two colors
        const newColor = bellColor === 'grey' ? 'green' : 'grey';
        setBellColor(newColor);
    };
    const [searchTerm, setSearchTerm] = useState("");
    const handleSearchChange = (event) => {
        const capitalize = (str) => {
            return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
        }

        const newSearchTerm = event.target.value;
        setSearchTerm(capitalize(newSearchTerm));
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

    const SidebarContent = (props) => (
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
                <NavItem icon={MdMedication} onClick={integrations.onToggle}>
                    Medications
                    <Icon
                        as={MdKeyboardArrowRight}
                        ml="auto"
                        transform={integrations.isOpen && "rotate(90deg)"}
                    />
                </NavItem>
                <Collapse in={integrations.isOpen}>
                    {fetchMedications && medications.map((medication, index) => (
                        <NavItem key={index} pl="12" py="2">
                            {medication.brandName}
                        </NavItem>
                    ))}
                </Collapse>
                <NavItem icon={CgAdd}>
                    <DrawerForm fetchMedications={fetchMedications}/>
                </NavItem>
                <NavItem icon={BsGearFill}>Settings</NavItem>
            </Flex>
        </Box>
    );

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
                        <Input name="search" placeholder="Search for Medications..." onChange={handleSearchChange}/>
                    </InputGroup>

                    <Flex onClick={() => {
                        handleBellClick()
                        alert("Notification On!")
                    }}>
                        <Icon as={FaBell} cursor="pointer" boxSize={6} color={bellColor}/>
                    </Flex>
                </Flex>
                <Box as="main" p="5">
                    <Wrap justify='left' spacing='20px'>
                        {fetchMedications && medications.length > 0 && searchTerm.length === 0 ? (
                            medications.map((medication, index) => (
                                <WrapItem key={index}>
                                    <MedsCard {...medication}/>
                                </WrapItem>
                            ))
                        ) : (
                            medications && medications.some(med => med.brandName === searchTerm) ? (
                                medications
                                    .filter(filteredMed => filteredMed.brandName === searchTerm)
                                    .map((filteredMedication, index) => (
                                        <WrapItem key={index}>
                                            <MedsCard {...filteredMedication} />
                                        </WrapItem>
                                    ))
                            ) : (
                                <Text>No Medication found</Text>
                            )
                        )}
                    </Wrap>
                </Box>
            </Box>
        </Box>
    );
};
export default SideBarWithNavBar;