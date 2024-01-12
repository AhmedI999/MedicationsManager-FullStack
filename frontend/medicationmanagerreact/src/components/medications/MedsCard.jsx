import {
    Box,
    Flex,
    chakra,
} from '@chakra-ui/react';
import MoreDetailsPopover from "./MoreDetailsPopover.jsx";
import DeleteMedicationConfirmation from "../medications/DeleteMedicationConfrimation.jsx";
import EditMedicationDrawer from "./EditMedicationDrawer.jsx";

const MedsCard = ( { pictureUrl,
                       activeIngredient,
                       brandName,
                       id ,
                       instructions,
                       medicineNumber,
                       timesDaily,
                       fetchMedications,
                       patientId } ) => {
    return (
        <Flex
            bg="#edf3f8"
            _dark={{
                bg: "#3e3e3e",
            }}
            p={1}
            w="full"
            alignItems="center"
            justifyContent="center"
        >
            <Flex
                w="2xl"
                mx="auto"
                bg="white"
                _dark={{
                    bg: "gray.800",
                }}
                shadow="lg"
                rounded="lg"
                overflow="hidden"
            >
                <Box
                    w={2 / 5}
                    bgSize="100% 100%"
                    style={{
                        backgroundImage: `url('${pictureUrl}')`,
                    }}
                >
                </Box>

                <Box
                    w={3 / 5}
                    p={{
                        base: 5,
                        md: 10,
                    }}
                >
                    <chakra.h1
                        fontSize="xl"
                        fontWeight="bold"
                        color="gray.800"
                        _dark={{
                            color: "white",
                        }}
                    >
                        {medicineNumber}. {brandName}
                    </chakra.h1>
                    <chakra.p
                        mt={2}
                        p={1}
                        fontSize="lg"
                        fontWeight="bold"
                        color="red.600"
                        _dark={{
                            color: "gray.400",
                        }}

                    >
                        {instructions}
                    </chakra.p>
                    <chakra.p
                        mt={2}
                        p={1}
                        fontSize="lg"
                        fontWeight="bold"
                        color="red.600"
                        _dark={{
                            color: "gray.400",
                        }}

                    >
                        {timesDaily} times a day.
                    </chakra.p>
                    <Flex mt={2} alignItems="center" justifyContent="space-between">
                        <MoreDetailsPopover>
                            <chakra.button
                                pictureUrl={pictureUrl}
                                activeIngredient={activeIngredient}
                                brandName={brandName}
                                id={id}
                                instructions={instructions}
                                medicineNumber={medicineNumber}
                                timesDaily={timesDaily}
                                patientId = {patientId}
                            />
                        </MoreDetailsPopover>
                        <EditMedicationDrawer
                            pictureUrl={pictureUrl}
                            activeIngredient={activeIngredient}
                            brandName={brandName}
                            id={id}
                            instructions={instructions}
                            timesDaily={timesDaily}
                            fetchMedications={fetchMedications}
                            patientId={patientId}
                        />
                        <DeleteMedicationConfirmation
                            brandName={brandName}
                            id={id}
                            fetchMedications={fetchMedications}
                        />
                    </Flex>
                </Box>
            </Flex>
        </Flex>
    )
};
export default MedsCard;