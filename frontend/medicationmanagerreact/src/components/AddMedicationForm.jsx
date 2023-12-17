import {Formik, Form, useField, FieldArray, Field} from 'formik';
import * as Yup from 'yup';
import {
    Input,
    Select,
    Checkbox,
    Button,
    FormControl,
    FormLabel,
    FormErrorMessage, Text, Textarea, Stack, Box,
} from '@chakra-ui/react';
import {MdTextFields} from "react-icons/md";
import ApplicationTermsAndConditions from "./ApplicationTermsAndConditions.jsx";
import {saveMedicine} from "../services/client.js";

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

const CheckboxInput = ({ children, ...props }) => {
    const [field, meta] = useField({ ...props, type: 'checkbox' });
    return (
        <FormControl isInvalid={meta.touched && meta.error}>
            <Checkbox {...field} {...props}>
                {children}
            </Checkbox>
            <FormErrorMessage>{meta.error}</FormErrorMessage>
        </FormControl>
    );
};

const SelectInput = ({ label, ...props }) => {
    const [field, meta] = useField(props);
    return (
        <FormControl isInvalid={meta.touched && meta.error}>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Select {...field} {...props} />
            <FormErrorMessage>{meta.error}</FormErrorMessage>
        </FormControl>
    );
};

const AddMedicationForm = () => {
    return (
        <>
            <Formik
                initialValues={{
                    pictureUrl: 'https://i.imgur.com/qMA0qhd.png',
                    brandName: '',
                    activeIngredient: '',
                    timesDaily: 0,
                    instructions: '',
                    interactions:[''],
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
                    acceptedTerms: Yup.boolean()
                        .required("You have to Accept the terms")
                })}
                validateOnMount={true}
                onSubmit={(medicine, { setSubmitting }) => {
                    setSubmitting(true);
                    saveMedicine(medicine)
                        .then( res => {
                            console.log(res);
                            alert("MedicineSaved");
                        }).catch( err => {
                        console.error(err);
                    }).finally( ()=> {
                        setSubmitting(false)
                    })
                }}
            >
                {({  values, isValid , isSubmitting}) => (
                    <Form>
                        <Stack spacing="15px">
                            <TextInput label="Picture Url" name="pictureUrl" type="text" placeholder="https://i.imgur.com/qMA0qhd.png" />
                            <TextInput label="Brand name" name="brandName" type="text" placeholder="Nevilob" />
                            <TextInput label="Active ingredient" name="activeIngredient" type="text" placeholder="Nebivolol" />
                            <TextInput label="Times daily" name="timesDaily" type="number" placeholder="1" />
                            <TextInput label="Instructions" name="instructions" type="text" placeholder="Take In the morning" />
                            {/*For Interactions Array*/}
                            <Text>Interactions</Text>
                            <FieldArray
                                name="interactions"
                                render={(arrayHelpers) => (
                                    <Stack spacing="5">
                                        {values.interactions.map((interaction, index) => (
                                            <Stack key={index} direction="row" align="center">
                                                <Field
                                                    name={`interactions.${index}`}
                                                    as={Input}
                                                    type="text"
                                                    placeholder={`Interaction ${index + 1}`}
                                                    key={`interaction.${index}`}
                                                />
                                                <Button
                                                    type="button"
                                                    colorScheme="red"
                                                    onClick={() => arrayHelpers.remove(index)}
                                                >
                                                    Remove
                                                </Button>
                                                {index === values.interactions.length - 1 && (
                                                    <Button
                                                        type="button"
                                                        colorScheme="green"
                                                        onClick={() => arrayHelpers.insert(index + 1, '')}
                                                    >
                                                        Add
                                                    </Button>
                                                )}
                                            </Stack>
                                        ))}
                                        {values.interactions.length === 0 && (
                                            <Stack key={0} direction="row" align="center">
                                                <Field
                                                    name="interactions.0"
                                                    as={Input}
                                                    type="text"
                                                    placeholder={`Interaction 1`}
                                                    key={`interaction.0`} // Add a unique key
                                                />
                                                <Button
                                                    type="button"
                                                    colorScheme="green"
                                                    onClick={() => arrayHelpers.insert(1, '')}
                                                >
                                                    Add
                                                </Button>
                                            </Stack>
                                        )}
                                    </Stack>
                                )}
                            />

                            <CheckboxInput name="acceptedTerms" >I accept the terms and conditions</CheckboxInput>
                            <ApplicationTermsAndConditions/>
                            <Button isDisabled={ !isValid || isSubmitting } type="submit" mt={1} >Submit</Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        </>
    );
};

export default AddMedicationForm;
// Depression drugs, Heart drugs, Irregular heart rhythm drugs, Other high blood pressure drugs, and Sildenafil