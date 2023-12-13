import {
    Box,
    Flex,
    Text,
    Icon,
    Collapse,
    useDisclosure,
    Drawer,
    DrawerOverlay,
    DrawerContent,
    IconButton,
    InputGroup,
    InputLeftElement,
    Input,
    Avatar,
    useColorModeValue,
    Image
} from '@chakra-ui/react';

// Import icons from the appropriate libraries
import {FiMenu, FiSearch} from 'react-icons/fi';
import {FaBell, FaClipboardCheck} from 'react-icons/fa'
import {MdHome, MdKeyboardArrowRight, MdMedication} from 'react-icons/md'
import {BsGearFill} from 'react-icons/bs'


const SideBarWithNavBar = ( {children} ) => {
    const sidebar = useDisclosure();
    const integrations = useDisclosure();
    const color = useColorModeValue("gray.600", "gray.300");

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
                    <NavItem pl="12" py="2">
                        Will be replaced with meds list from db
                    </NavItem>
                    <NavItem pl="12" py="2">
                        **
                    </NavItem>
                    <NavItem pl="12" py="2">
                        **
                    </NavItem>
                </Collapse>
                <NavItem icon={FaClipboardCheck}>TO-DO</NavItem>
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
                        <Input placeholder="Search for Medications..."/>
                    </InputGroup>
                    {/*navbar profile pic and alarm for meds notification*/}
                    <Flex align="center">
                        <Icon color="gray.500" as={FaBell} cursor="pointer"/>
                        <Avatar
                            ml="4"
                            size="sm"
                            name="anubra266"
                            src="https://avatars.githubusercontent.com/u/30869823?v=4"
                            cursor="pointer"
                        />
                    </Flex>
                </Flex>

                <Box
                    as="main"
                    p="4"
                    display="flex"
                    flexDirection="column"
                    flex="1"
                >
                    <Box borderWidth="4px" borderStyle="dashed" rounded="md" h="96">
                        {children}
                    </Box>
                </Box>
            </Box>
        </Box>
    );
};
export default SideBarWithNavBar;