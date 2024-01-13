import {
    Button,
    Divider,
    FormControl, FormErrorMessage, FormLabel, Input, Stack

} from "@chakra-ui/react";
import { Form, Formik, useField} from "formik";
import * as Yup from "yup";
import {editPatientPassword} from "../../../services/client.js";
import {errorNotification, successNotification} from "../../../services/Notifications.js";
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
const ChangePasswordForm = ({id, logout}) => {
        return (
            <Formik
                initialValues={{
                    currentPassword: '',
                    password: ''
                }}
                validationSchema={Yup.object({
                    currentPassword: Yup.string()
                        .trim()
                        .required("Current Password is required"),
                    password: Yup.string()
                        .trim()
                        .matches(/^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$/, "Password should contain at least 1 uppercase and 1 special Character")
                        .min(6, "Password should be at least 6 characters")
                        .required("New Password is Required")
                })}
                onSubmit={( values, {setSubmitting}) => {
                    setSubmitting(true);
                    // handle the change in details after success edit
                    editPatientPassword(id, values)
                        .then( () => {
                            successNotification(
                                `Changing your Password`,
                                `Password has been updated successfully
                                Sign in with the new password`
                            );
                            logout();
                        })
                        .catch( (err) => {
                            errorNotification(
                                `Changing your Password`,
                                `Couldn't Change Your Password. Error ${err.code}: ${err.response.data.message}`
                            );
                        })
                        .finally(() => {
                            setSubmitting(false);
                        });
                }}
            >
                {({isValid, dirty, isSubmitting}) => (
                    <Form>
                        <Stack spacing="15px">
                            <TextInput label="Current Password" name="currentPassword" type="password" placeholder="Old Password"/>
                            <TextInput label="New Password" name="password" type="password" placeholder="New Password"/>

                            <Divider/>

                            <Button isDisabled={isValid && !dirty || isSubmitting} type="submit" mt={1}>
                                Change Password
                            </Button>
                        </Stack>
                    </Form>
                )}
            </Formik>
        );
};
export default ChangePasswordForm;