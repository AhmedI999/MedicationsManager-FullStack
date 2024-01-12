import {
    Button,
    Flex,
    FormLabel,
    Heading,
    Input,
    Stack,
    Image, Box, Alert, AlertIcon,
} from '@chakra-ui/react'
import {Form, Formik, useField} from "formik";
import * as Yup from 'yup';
import {useAuth} from "../context/AuthContext.jsx";
import {errorNotification} from "../../services/Notifications.js";
import {useNavigate} from "react-router-dom";
import {setCookie} from "../../services/cookieUtils.js";

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

const LoginForm = () => {
    const {login, getPatient} = useAuth();
    const navigate = useNavigate();

    return (
        <Formik
            validateOnMount={true}
            validationSchema={
                Yup.object({
                    email: Yup.string()
                        .email("Must be a valid email")
                        .required("Email is required"),
                    password: Yup.string()
                        .required("Password is required")
            })
            }
            initialValues={{email: '', password: ''}}
                onSubmit={(values, { setSubmitting }) => {
                    setSubmitting(true);
                    login(values).then( () => {
                        // getting patient information
                        getPatient(values.email).then( res => {
                            navigate("/medications")
                            setCookie('i', res.data.id, 1);
                        })
                    }).catch( err => {
                        errorNotification("Login error",
                            `Login failed ${err.code}. ${err.message}`)
                    }).finally( () => {
                        setSubmitting(false);
                    })
                }}>
            {({ isValid, isSubmitting})=> (
                <Form>
                    <Stack spacing={15}>
                        <MyTextInput
                            label={"Email"}
                            name={"email"}
                            type={"email"}
                            placeholder={"john@example.com"}
                        />
                        <MyTextInput
                            label={"Password"}
                            name={"password"}
                            type={"password"}
                            placeholder={"Type your password"}
                        />
                        <Button isDisabled={ !isValid || isSubmitting } type="submit">Login</Button>
                    </Stack>
                </Form>
            )}
        </Formik>
    )
};

const Login = () => {
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
                    <Heading fontSize={'2xl'} mb={15}>Sign in to your account</Heading>
                    <LoginForm />
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
export default Login;