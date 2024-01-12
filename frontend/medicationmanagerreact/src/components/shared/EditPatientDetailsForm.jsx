    import {Button, Divider, FormControl, FormErrorMessage, FormLabel, Input, Stack} from "@chakra-ui/react";
    import * as Yup from "yup";
    import {editPatient} from "../../services/client.js";
    import {errorNotification, successNotification} from "../../services/Notifications.js";
    import {useNavigate} from "react-router-dom";
    import {Field, Form, Formik} from "formik";


    const TextInput = ({label, name, type}) => (
        <Field name={name}>
            {({field, meta}) => (
                <FormControl>
                    <FormLabel>{label}</FormLabel>
                    <Input
                        {...field}
                        type={type}
                    />
                    {meta.touched && meta.error && (
                        <FormErrorMessage>{meta.error}</FormErrorMessage>
                    )}
                </FormControl>
            )}
        </Field>
    );

    const EditPatientDetailsForm = ({patientData, refresh, close, logout}) => {
        const navigate = useNavigate();
        const {
            id,
            email,
            firstname,
            lastname,
            age
        } = patientData.data;

        return (
            <Formik
                initialValues={{
                    email: email || '',
                    firstname: firstname || '',
                    lastname: lastname || '',
                    age: age || 0,
                }}
                validationSchema={Yup.object({
                    email: Yup.string()
                        .trim()
                        .email()
                        .required("Email is required."),
                    firstname: Yup.string()
                        .trim()
                        .required("First name can't be empty"),
                    lastname: Yup.string()
                        .trim()
                        .required("Last name can't be empty"),
                    age: Yup.number()
                        .min(1),
                })}
                onSubmit={(patient, {setSubmitting}) => {
                    setSubmitting(true);
                    // Check if the email field has changed
                    const isEmailChanged = patient.email !== email;
                    // handle the change in details after success edit
                    editPatient(id, patient)
                        .then((res) => {
                            successNotification(
                                `Updating Your Information`,
                                `Your Details have been updated successfully`
                            );
                            // Call the refresh to update the user info
                            refresh(res);
                            close();
                        })
                        .catch((err) => {
                            errorNotification(
                                `Updating Your Information`,
                                `Couldn't Edit Your Details. Error ${err.code}: ${err.response.data.message}`
                            );
                        })
                        .finally(() => {
                            if (isEmailChanged) {
                                logout();
                            }
                            setSubmitting(false);
                        });
                }}
            >
                {({isValid, dirty, isSubmitting}) => (
                    <Form>
                        <Stack spacing="15px">
                            <TextInput label="Email" name="email" type="text"/>
                            <TextInput label="Firstname" name="firstname" type="text"/>
                            <TextInput label="Lastname" name="lastname" type="text"/>
                            <TextInput label="Age" name="age" type="number"/>

                            <Divider/>

                            <Button isDisabled={isValid && !dirty || isSubmitting} type="submit" mt={1}>
                                Submit
                            </Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        );
    };

    export default EditPatientDetailsForm;
    // currentPassword: Yup.string()
    //     .trim(),
    //     password: Yup.string()
    //     .trim()
    //     .optional()
    //     .matches("/^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$/", "Password must contain at least 1 Uppercase character and 1 special character"),