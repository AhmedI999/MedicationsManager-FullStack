import {Formik, Form, useField} from 'formik';
import * as Yup from 'yup';
import {
    Input,
    Button,
    FormControl,
    FormLabel,
    FormErrorMessage, Stack
} from '@chakra-ui/react';
import {editMedication} from "../services/client.js";
import {errorNotification, successNotification} from "../services/Notifications.js";

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

const EditMedicationForm = ( {   pictureUrl,
                                activeIngredient,
                                brandName,
                                id ,
                                instructions,
                                timesDaily,
                                fetchMedications } ) => {
    return (
        <>
            <Formik
                initialValues={{
                    pictureUrl: `${pictureUrl}`,
                    brandName: `${brandName}`,
                    activeIngredient: `${activeIngredient}`,
                    timesDaily: `${timesDaily}`,
                    instructions: `${instructions}`,
                }}
                validationSchema={Yup.object({
                    pictureUrl: Yup.string()
                        .trim()
                        .url('Invalid URL')
                        .matches(/\.(jpeg|jpg|gif|png)$/i, 'Invalid image URL')
                        .required("Please leave the default picture if you don't want to add an image"),
                    brandName: Yup.string()
                        .trim()
                        .required('Brand name is required'),
                    timesDaily: Yup.number()
                        .min(1, "Times must be greater than 0")
                        .required('Times medicine taken daily is required'),
                    instructions: Yup.string()
                        .trim()
                        .required('For safety reasons, instructions are required'),
                })}
                onSubmit={(medication, { setSubmitting}) => {
                    setSubmitting(true);
                    editMedication(1, id, medication)
                        .then( () => {
                            successNotification(
                                `Editing ${brandName} Information`,
                                `${brandName} Details have been updated successfully`
                            )
                        }).catch( err => {
                        errorNotification(
                            `Editing ${brandName} Information`,
                            `Couldn't Edit ${brandName}. Error ${err.code}: ${err.response.data.message}`
                        )
                    }).finally( ()=> {
                        fetchMedications();
                        setSubmitting(false)
                    })
                }}
            >
                {({ isValid , isSubmitting, dirty}) => (
                    <Form>
                        <Stack spacing="15px">
                            <TextInput label="Picture Url" name="pictureUrl" type="text" placeholder="https://i.imgur.com/qMA0qhd.png" />
                            <TextInput label="Brand name" name="brandName" type="text" placeholder="Nevilob" />
                            <TextInput label="Active ingredient" name="activeIngredient" type="text" placeholder="Nebivolol" />
                            <TextInput label="Times daily" name="timesDaily" type="number" placeholder="1" />
                            <TextInput label="Instructions" name="instructions" type="text" placeholder="Take In the morning" />
                            <Button isDisabled={ !(isValid && dirty) || isSubmitting } type="submit" mt={1}>Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default EditMedicationForm;