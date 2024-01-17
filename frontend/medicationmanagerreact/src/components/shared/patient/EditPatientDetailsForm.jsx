import {
    Alert,
    AlertIcon,
    Box,
    Button,
    Divider,
    FormLabel,
    Input,
    Stack
} from "@chakra-ui/react";
import * as Yup from "yup";
import {Form, Formik, useField} from "formik";
import CloseAccount from "./CloseAccount.jsx";
import {editPatient} from "../../../services/client.js";
import {errorNotification, successNotification} from "../../../services/Notifications.js";


const TextInput = ({label, ...props}) => {
    const [field, meta] = useField(props);
    return (
        <Box>
            <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
            <Input className="text-input" {...field} {...props} />
            {meta.touched && meta.error ? (
                <Alert className="error" status={"error"} mt={2}>
                    <AlertIcon/>
                    {meta.error}
                </Alert>
            ) : null}
        </Box>
    );
};

const EditPatientDetailsForm = ({patientData, refresh, close, logout}) => {
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
                age: age,
            }}
            validationSchema={Yup.object({
                email: Yup.string()
                    .trim()
                    .email()
                    .required("Email is required."),
                firstname: Yup.string()
                    .trim()
                    .matches(/^[A-Za-z]+$/, "Only alphabetic characters are allowed")
                    .required("First name can't be empty"),
                lastname: Yup.string()
                    .matches(/^[A-Za-z]+$/, "Only alphabetic characters are allowed")
                    .required("Last name can't be empty"),
                age: Yup.number()
                    .max(110, "Age must be a Valid number")

            })}
            validateOnMount={true}
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
                            `Couldn't Edit Your Details. Error: ${err.response.data.message}`
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

                        <Button isDisabled={ !isValid && dirty || isSubmitting } type="submit" mt={1}>
                            Submit
                        </Button>
                        <Divider/>
                        <CloseAccount />
                    </Stack>
                </Form>
            )}
        </Formik>
    );
};

export default EditPatientDetailsForm;