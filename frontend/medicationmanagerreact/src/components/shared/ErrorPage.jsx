import {Box, ChakraProvider, Text, Link} from "@chakra-ui/react";

const ErrorPage = () => {
    return (
        <ChakraProvider>
            <Box
                height="100vh"
                display="flex"
                flexDirection="column"
                alignItems="center"
                justifyContent="center"
                bg="#342643"
                padding="10px"
                color="white"
                fontFamily="Raleway, sans-serif"
            >
                <Text fontSize="5xl" fontWeight="700" color="#EE4B5E">
                    403 - ACCESS DENIED
                </Text>

                <Text fontSize="40px" fontWeight="700" color="#1FA9D6">
                    Oops, You don't have permission to access this page.
                </Text>

                <Link
                    href="/"
                    textDecoration="none"
                    fontWeight="700"
                    border="2px solid #EE4B5E"
                    padding="15px"
                    textTransform="uppercase"
                    color="#EE4B5E"
                    borderRadius="26px"
                    transition="all 0.2s ease-in-out"
                    display="inline-block"
                    _hover={{ backgroundColor: '#EE4B5E', color: 'white' }}
                >
                    Go to Login
                </Link>
            </Box>
        </ChakraProvider>
    );
};
export default ErrorPage;