import {
    Button,
    Flex,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Box, Alert, AlertIcon, FormControl, Checkbox, FormErrorMessage, Link
} from '@chakra-ui/react'
import {Form, Formik, useField} from "formik";
import * as Yup from 'yup';
import {successNotification} from "../../services/Notifications.js";
import {useNavigate} from "react-router-dom";
import {savePatient} from "../../services/client.js";
import ApplicationTermsAndConditions from "../shared/utils/ApplicationTermsAndConditions.jsx";

const MyTextInput = ({label, ...props}) => {
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

const CreateAccountForm = () => {
    const navigate = useNavigate();

    return (
        <Formik
            initialValues={{
                email: '',
                password: '',
                firstname: '',
                lastname: '',
                age: ''
            }}
            validationSchema={
                Yup.object({
                    email: Yup.string()
                        .trim()
                        .email()
                        .required("Email is required."),
                    password: Yup.string()
                        .trim()
                        .matches(/^(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$/,
                            "Password should contain at least 1 uppercase and 1 special Character")
                        .min(6, "Password should be at least 6 characters"),
                    firstname: Yup.string()
                        .trim()
                        .matches(/^[A-Za-z]+$/, "Only alphabetic characters are allowed")
                        .required("First name is required"),
                    lastname: Yup.string()
                        .trim()
                        .matches(/^[A-Za-z]+$/, "Only alphabetic characters are allowed")
                        .required("Last name is required"),
                    age: Yup.number()
                        .min(1, "Age must be at least 1")
                        .max(110, "Age must be a Valid number"),
                    acceptedTerms: Yup.boolean()
                        .required("You have to Accept the terms")
                })
            }
            validateOnMount={true}
            onSubmit={(newPatient, { setSubmitting }) => {
                setSubmitting(true);
                // remove the last name if it is empty
                if (newPatient.lastname === ''){
                    delete newPatient.lastname
                }
                // remove age if not entered and if it is not valid, then parse the string if valid
                if (newPatient.age.trim() === '' || isNaN(parseInt(newPatient.age, 10))){
                    delete newPatient.age
                } else {
                    newPatient.age = parseInt(newPatient.age, 10);
                }
                savePatient(newPatient).then( () => {
                    successNotification("Creating Account",
                            "Account Created Successfully, Login in to continue")
                    navigate("/")
                }).finally( () => {
                    setSubmitting(false);
                })
            }}>
            {({ isValid, isSubmitting})=> (
                <Form>
                    <Stack spacing={15}>
                        <MyTextInput label={"Email *"} name={"email"} type={"email"} placeholder={"john@example.com"}/>
                        <MyTextInput label={"Password *"} name={"password"} type={"password"} placeholder={"Type your password"}/>
                        <MyTextInput label={"First Name *"} name={"firstname"} type={"text"} placeholder={"John"}/>
                        <MyTextInput label={"Last name *"} name={"lastname"} type={"text"} placeholder={"Doe"}/>
                        <MyTextInput label={"Age"} name={"age"} type={"number"}/>
                        <CheckboxInput name="acceptedTerms" >I accept the terms and conditions</CheckboxInput>
                        <ApplicationTermsAndConditions/>
                        <Button isDisabled={ !isValid || isSubmitting } type="submit">Create Account</Button>
                    </Stack>
                    <br/>
                    <Link color={"blue.400"} href={"/"}>Have an account? Login now!</Link>
                </Form>
            )}
        </Formik>
    )
};
const CreateAccount = () => {
    return (
        <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
            <Flex p={8} flex={1} align={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Image
                        src={"https://i.imgur.com/cwHOzAb.png"}
                        boxSize={"110px"}
                        ml={18}
                        alt={"Medication manager Logo"}
                    />
                    <Heading fontSize={'2xl'} mb={15}>Create a new Account</Heading>
                    <CreateAccountForm />
                </Stack>
            </Flex>
            <Flex flex={1} mt={4} mr={1} mb={4}>
                <Image
                    alt={'Login Image'}
                    objectFit={'fill'}
                    src={
                        'https://images.pexels.com/photos/40568/medical-appointment-doctor-healthcare-40568.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=1'
                    }
                />
            </Flex>
        </Stack>
    )
}
export default CreateAccount;