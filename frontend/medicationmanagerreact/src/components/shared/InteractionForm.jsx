import { Formik, Form, Field } from 'formik';
import * as Yup from 'yup';
import {
    Button,
    Stack,
    Slider,
    SliderTrack,
    SliderFilledTrack,
    SliderThumb,
    Text,
    FormControl,
    FormErrorMessage,
    Popover,
    PopoverTrigger,
    PopoverContent,
    PopoverHeader,
    PopoverBody,
    FormLabel,
    Input,
    useDisclosure,
} from '@chakra-ui/react';
import {saveMedicationInteraction} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/Notifications.js";
const InteractionForm = ({ medicationId, refetchInteractions }) => {
    const { isOpen, onOpen, onClose } = useDisclosure();
    return (
        <Popover isOpen={isOpen} onOpen={onOpen} onClose={onClose}>
            <PopoverTrigger>
                <Button colorScheme="blue" boxSize={6}>+</Button>
            </PopoverTrigger>

            <PopoverContent>
                <PopoverHeader>Interaction Form</PopoverHeader>
                <PopoverBody>
                    <Formik
                        initialValues={{
                            name: '',
                            type: 'MILD',
                        }}
                        validationSchema={Yup.object({
                            name: Yup.string().required('Interaction name is required'),
                        })}

                        onSubmit={(interaction, { setSubmitting }) => {
                            setSubmitting(true);

                            saveMedicationInteraction(
                                { ...interaction, severity: interaction.type },
                                1,
                                medicationId
                            )
                                .then( () => {
                                    successNotification(
                                        `Adding interaction ${interaction.name}`,
                                        `Interaction ${interaction.name} has been added successfully`
                                    )

                                }).catch((err) => {
                                    errorNotification(
                                        `Adding interaction ${interaction.name}`,
                                        `Couldn't Add ${interaction.name}. Error ${err.code}: ${err.response.data.message}`
                                    )
                                }).finally( ()=> {
                                refetchInteractions();
                                setSubmitting(false);
                            })
                        }}
                    >
                        {({ isValid, isSubmitting, values, setFieldValue }) => (
                            <Form>
                                <Stack spacing="15px">
                                    <FormControl isInvalid={!isValid} mb={4}>
                                        <FormLabel htmlFor="name">Interaction Name</FormLabel>
                                        <Field name="name">
                                            {({ field, meta }) => (
                                                <>
                                                    <Input
                                                        {...field}
                                                        type="text"
                                                        placeholder="Enter interaction name"
                                                        borderColor={isValid ? 'gray.300' : 'red.500'}
                                                        borderWidth="1px"
                                                        borderRadius="md"
                                                        p={2}
                                                        _focus={{
                                                            borderColor: 'blue.500',
                                                            boxShadow: 'outline',
                                                        }}
                                                    />
                                                    <FormErrorMessage>{meta.error}</FormErrorMessage>
                                                </>
                                            )}
                                        </Field>
                                    </FormControl>

                                    <FormControl>
                                        <FormLabel htmlFor="type">Severity Level</FormLabel>
                                        <Slider
                                            name="type"
                                            value={values.type === 'MILD' ? 0 : values.type === 'MODERATE' ? 1 : 2}
                                            min={0}
                                            max={2}
                                            step={1}
                                            colorScheme="red"
                                            onChange={(value) => {
                                                const severityOptions = ['MILD', 'MODERATE', 'SEVERE'];
                                                setFieldValue('type', severityOptions[value]);
                                            }}
                                        >
                                            <SliderTrack bg="gray.100">
                                                <SliderFilledTrack bg="red.500" />
                                            </SliderTrack>
                                            <SliderThumb boxSize={3} bg="blue.500" />
                                        </Slider>
                                        <Text mt={2} textAlign="center" color="red.500" fontWeight="bold">
                                            {values.type}
                                        </Text>
                                    </FormControl>

                                    <Button
                                        isDisabled={!isValid || isSubmitting}
                                        type="submit"
                                        mt={0}
                                        colorScheme="blue"
                                        onClick={onClose}
                                    >
                                        Save
                                    </Button>
                                </Stack>
                            </Form>
                        )}
                    </Formik>
                </PopoverBody>
            </PopoverContent>
        </Popover>
    );
};
export default InteractionForm;