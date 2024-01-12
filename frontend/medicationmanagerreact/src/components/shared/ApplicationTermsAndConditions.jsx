import {
    Accordion, AccordionButton, AccordionIcon, AccordionItem, AccordionPanel, Box,
    Button, Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerHeader,
    DrawerOverlay, useDisclosure
} from "@chakra-ui/react";

const ApplicationTermsAndConditions = () => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    return (
        <>
        <Drawer isOpen={isOpen} onClose={onClose} placement={"left"} size={"xl"}>
            <DrawerOverlay/>
            <DrawerContent>
                <DrawerCloseButton/>
                <DrawerHeader>Terms and Conditions</DrawerHeader>
                <DrawerBody>
                    Disclaimer: Medication Manager Beta Program
                    This Medication Manager app is currently in beta, and as such,
                    it may have certain limitations and undergo continuous improvements.
                    We would like to inform users that the app is provided "as is" and without any warranties.

                    <Accordion>
                        <AccordionItem>
                            <h2>
                                <AccordionButton>
                                    <Box as="span" flex='1' textAlign='left'>
                                        Responsibility
                                    </Box>
                                    <AccordionIcon />
                                </AccordionButton>
                            </h2>
                            <AccordionPanel pb={4}>
                                The Medication Manager app is not intended to replace professional medical advice, diagnosis, or treatment.
                                Users should consult with qualified healthcare professionals for accurate and personalized medical guidance.
                                The app is a tool designed to assist users in managing their medications and related information.
                            </AccordionPanel>
                        </AccordionItem>

                        <AccordionItem>
                            <h2>
                                <AccordionButton>
                                    <Box as="span" flex='1' textAlign='left'>
                                        Not a Substitute
                                    </Box>
                                    <AccordionIcon />
                                </AccordionButton>
                            </h2>
                            <AccordionPanel pb={4}>
                                The Medication Manager app is not a substitute for the expertise, skill, knowledge, and judgment of healthcare practitioners.
                                Users are encouraged to verify any information obtained from the app and consult with healthcare professionals to ensure its accuracy.
                            </AccordionPanel>
                        </AccordionItem>
                        <AccordionItem>
                        <h2>
                            <AccordionButton>
                                <Box as="span" flex='1' textAlign='left'>
                                    Beta Program
                                </Box>
                                <AccordionIcon />
                            </AccordionButton>
                        </h2>
                        <AccordionPanel pb={4}>
                            Being in beta means that the app is still undergoing testing and development.
                            Users are encouraged to provide feedback on any issues, glitches, or suggestions for improvement.
                            The app developers are actively working to enhance its features and address any reported issues.
                        </AccordionPanel>
                    </AccordionItem>
                    <AccordionItem>
                        <h2>
                            <AccordionButton>
                                <Box as="span" flex='1' textAlign='left'>
                                    Liability
                                </Box>
                                <AccordionIcon />
                            </AccordionButton>
                        </h2>
                        <AccordionPanel pb={4}>
                            The developers of the Medication Manager app disclaim any liability for direct,
                            indirect, incidental, or consequential damages arising out of the use, or inability to use, the app.
                            This includes but is not limited to loss of data, profits, or business interruption.
                        </AccordionPanel>
                    </AccordionItem>
                    <AccordionItem>
                        <h2>
                            <AccordionButton>
                                <Box as="span" flex='1' textAlign='left'>
                                    Reliability
                                </Box>
                                <AccordionIcon />
                            </AccordionButton>
                        </h2>
                        <AccordionPanel pb={4}>
                            While efforts are made to ensure the reliability of the app,
                            the developers do not guarantee that it will be error-free or uninterrupted.
                            Users are advised to use the app responsibly and not solely rely on it for critical healthcare decisions.
                        </AccordionPanel>
                    </AccordionItem>
                    <AccordionItem>
                        <h2>
                            <AccordionButton>
                                <Box as="span" flex='1' textAlign='left'>
                                    Acceptance of Terms
                                </Box>
                                <AccordionIcon />
                            </AccordionButton>
                        </h2>
                        <AccordionPanel pb={4}>
                            By using the Medication Manager app, users acknowledge that they have read and understood this disclaimer.
                            They agree to use the app at their own risk and accept any associated risks and responsibilities.
                        </AccordionPanel>
                    </AccordionItem>
                    <AccordionItem>
                        <h2>
                            <AccordionButton>
                                <Box as="span" flex='1' textAlign='left'>
                                    Contact And Rights
                                </Box>
                                <AccordionIcon />
                            </AccordionButton>
                        </h2>
                        <AccordionPanel pb={4}>
                            For inquiries, feedback, or assistance, please contact ahmedibrahim5182@gmail.com.
                            Thank you for being a part of the Medication Manager beta program.
                            <br/>
                            2024 Simple Solutions. All Rights Reserved
                        </AccordionPanel>
                    </AccordionItem>
                    </Accordion>
                </DrawerBody>
            </DrawerContent>
        </Drawer>
            <Button onClick={onOpen}>
                View terms and conditions
            </Button>
        </>
    )
};
export default ApplicationTermsAndConditions;