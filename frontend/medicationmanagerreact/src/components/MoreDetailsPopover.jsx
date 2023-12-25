import {
    Button,
    Popover,
    PopoverTrigger,
    PopoverContent,
    PopoverHeader,
    PopoverBody,
    PopoverFooter,
    chakra, Flex, List, ListItem, ListIcon, useDisclosure, Spinner, Text
} from "@chakra-ui/react";
import {MdOutlineMedication} from "react-icons/md";
import {useEffect, useState} from "react";
import {getMedicationInteractions} from "../services/client.js";
import SideBarWithNavBar from "./shared/SideBarWithNavBar.jsx";
import useMedicationInteractions from "../services/useMedicationInteractions.jsx";
import InteractionForm from "./shared/InteractionForm.jsx";
import DeleteMedicationInteractionConfirmation from "./shared/DeleteMedicationInteractionConfrimation.jsx";

const MoreDetailsPopover = ({children}) => {
    const {
        pictureUrl,
        activeIngredient,
        brandName,
        id,
        instructions,
        medicineNumber,
        timesDaily,
    } = children.props;
    const { isOpen, onOpen, onClose } = useDisclosure();
    const { interactions, loading, refetchInteractions } = useMedicationInteractions(id);
    const [shouldRefetch, setShouldRefetch] = useState(false);

    useEffect(() => {
        if (shouldRefetch){
            setShouldRefetch(true);
        } else {
            setShouldRefetch(false);
        }
    }, [interactions]);

    if (loading) {
        return (
            <SideBarWithNavBar>
                <Spinner
                    thickness='4px'
                    speed='0.65s'
                    emptyColor='gray.200'
                    color='blue.500'
                    size='xl'
                />
            </SideBarWithNavBar>
        )
    }


    return (
        <Popover isOpen={isOpen} onOpen={onOpen} onClose={onClose}>
            <PopoverTrigger>
                <Button
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
                    mx={16}
                >
                    More Details
                </Button>
            </PopoverTrigger>
            <PopoverContent w="full" medicationId={id}>
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

                    { interactions && interactions.length > 0 ? (
                        <chakra.div mt={4}>
                            <chakra.h3 fontSize="md" fontWeight="bold" mb={2}>
                                Interactions:
                            </chakra.h3>
                            <List listStyleType="none" pl={0}>
                                { interactions.map((interaction, index) => (
                                    <ListItem key={`interaction-${index}`} w="full">
                                        <ListIcon as={MdOutlineMedication} color="green.500" />
                                        {interaction.name} | {(interaction.type).toLowerCase()}
                                        <DeleteMedicationInteractionConfirmation
                                            {...interaction}
                                            medicationId={id}
                                            refetchInteractions={refetchInteractions}
                                        />
                                    </ListItem>
                                ))}
                            </List>
                        </chakra.div>
                    ) : (
                        <chakra.div mt={4}>
                            <Text>No Interactions added</Text>
                        </chakra.div>
                    )}
                    <InteractionForm
                        medicationId={id}
                        refetchInteractions={refetchInteractions}
                    />
                </PopoverBody>
                <PopoverFooter>
                    <Button onClick={onClose}>Close</Button>
                </PopoverFooter>
            </PopoverContent>
        </Popover>
    );
};

export default MoreDetailsPopover;
