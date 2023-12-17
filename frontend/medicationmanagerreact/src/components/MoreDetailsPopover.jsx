import {
    Button,
    Popover,
    PopoverTrigger,
    PopoverContent,
    PopoverHeader,
    PopoverBody,
    PopoverFooter,
    chakra, Flex, List, ListItem, ListIcon, useDisclosure
} from "@chakra-ui/react";
import {MdOutlineMedication} from "react-icons/md";

const MoreDetailsPopover = ({children}) => {
    const {
        pictureUrl,
        activeIngredient,
        brandName,
        id,
        instructions,
        interactions,
        medicineNumber,
        timesDaily,
    } = children.props;
    const { isOpen, onOpen, onClose } = useDisclosure();
    return (
        <Popover isOpen={isOpen} onOpen={onOpen} onClose={onClose}>
            <PopoverTrigger>
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
                    ml="auto"
                >
                    More Details
                </Flex>
            </PopoverTrigger>
            <PopoverContent>
                <PopoverHeader>More Details on {brandName}</PopoverHeader>
                <PopoverBody>
                    {activeIngredient.trim() !== '' && (
                        <chakra.p>
                            <strong>Active Ingredient:</strong> {activeIngredient}
                        </chakra.p>
                    )}

                    <chakra.p>
                        <strong>Medicine Number:</strong> {medicineNumber}
                    </chakra.p>

                    <chakra.p>
                        <strong>Times Daily:</strong> {timesDaily}
                    </chakra.p>

                    <chakra.p>
                        <strong>Instructions:</strong> {instructions}
                    </chakra.p>

                    {interactions && (
                        <chakra.div mt={4}>
                            <chakra.h3 fontSize="md" fontWeight="bold" mb={2}>
                                Interactions
                            </chakra.h3>
                            <List listStyleType="none" pl={0}>
                                {interactions.map((interaction, index) => (
                                    <ListItem key={`interaction-${index}`}>
                                        <ListIcon as={MdOutlineMedication} color='green.500'/>
                                        {interaction}
                                    </ListItem>
                                ))}
                            </List>
                        </chakra.div>
                    )}
                </PopoverBody>
                <PopoverFooter>
                    <Button onClick={onClose}>Close</Button>
                </PopoverFooter>
            </PopoverContent>
        </Popover>
    );
};

export default MoreDetailsPopover;
