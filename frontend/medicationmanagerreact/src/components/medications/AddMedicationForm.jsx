import {Formik, Form, useField} from 'formik';
import {
    Input,
    Button,
    FormControl,
    FormLabel,
    FormErrorMessage, Stack
} from '@chakra-ui/react';
import {saveMedication} from "../../services/client.js";
import {errorNotification, successNotification} from "../../services/Notifications.js";
import {createMedicationValidationSchema} from "../shared/utils/CreateMedicationValidationSchema.jsx";

const TextInput = ({ label, ...props }) => {
    const [field, meta] = useField(props);
    return (
        <FormControl isInvalid={meta.touched && meta.error}>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input {...field} {...props} />
            <FormErrorMessage>{meta.error}</FormErrorMessage>
        </FormControl>
    );
};

const AddMedicationForm = ({ fetchMedications, patientId}) => {
    return (
        <>
            <Formik
                initialValues={{
                    pictureUrl: 'https://i.imgur.com/qMA0qhd.png',
                    brandName: '',
                    activeIngredient: '',
                    timesDaily: 0,
                    instructions: '',
                }}
                validationSchema={createMedicationValidationSchema()}
                validateOnMount={true}
                onSubmit={(medicine, { setSubmitting }) => {
                    setSubmitting(true);
                    saveMedication(medicine, patientId)
                        .then( () => {
                            fetchMedications();
                            successNotification(
                                `Adding New Medication`,
                                `${medicine.brandName} Has been saved successfully`
                            )
                        }).catch( err => {
                        errorNotification(
                            `Adding New Medication`,
                            `Couldn't Add ${medicine.brandName}. Error ${err.code}: ${err.response.data.message}`
                        )
                    }).finally( ()=> {
                        fetchMedications();
                        setSubmitting(false)
                    })
                }}
            >
                {({ isValid , isSubmitting}) => (
                    <Form>
                        <Stack spacing="15px">
                            <TextInput label="Picture Url" name="pictureUrl" type="text" placeholder="https://i.imgur.com/qMA0qhd.png" />
                            <TextInput label="Brand name" name="brandName" type="text" placeholder="Nevilob" />
                            <TextInput label="Active ingredient" name="activeIngredient" type="text" placeholder="Nebivolol" />
                            <TextInput label="Times daily" name="timesDaily" type="number" placeholder="1" />
                            <TextInput label="Instructions" name="instructions" type="text" placeholder="Take In the morning" />
                            <Button isDisabled={ !isValid || isSubmitting } type="submit" mt={1}>Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default AddMedicationForm;