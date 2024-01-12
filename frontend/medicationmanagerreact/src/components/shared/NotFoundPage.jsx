import { Box, Button, Center, ChakraProvider, extendTheme, Image } from '@chakra-ui/react';
import { keyframes } from '@emotion/react';
import {useNavigate} from "react-router-dom";

const theme = extendTheme({
    fonts: {
        body: 'Dosis, sans-serif',
    },
});

const moveAstronaut = keyframes`
  100% { transform: translate(-200px, -200px); }
`;

const rotateAstronaut = keyframes`
  100% { transform: rotate(-720deg); }
`;

const purpleBackgroundStyle = {
    background: `url(https://salehriaz.com/404Page/img/bg_purple.png)`,
    backgroundRepeat: 'repeat-x',
    backgroundSize: 'full',
    backgroundPosition: 'left top',
    height: '100%',
    overflow: 'hidden',
};

const GlowingStar = ({ top, left, animationDelay }) => {
    const glowStar = keyframes`
        40% { opacity: 0.3; }
        90%, 100% { opacity: 1; transform: scale(1.2); }
    `;

    return (
        <Box
            className="star"
            style={{
                position: 'absolute',
                borderRadius: '100%',
                backgroundColor: '#fff',
                width: '3px',
                height: '3px',
                opacity: 0.5,
                willChange: 'opacity',
                top: top || '0%',
                left: left || '0%',
                animation: `${glowStar} 2s infinite ease-in-out alternate ${animationDelay || '0s'}`,
            }}
        ></Box>
    );
};

const NotFoundPage = () => {
    const navigate = useNavigate();
    return (
        <ChakraProvider theme={theme}>
            <Box style={purpleBackgroundStyle} h="100vh" overflow="hidden">
                <Box className="stars">
                    <Center className="central-body" padding="17% 5% 10% 5%" textAlign="center">
                        <Image className="image-404" src="https://salehriaz.com/404Page/img/404.svg" alt="404 Image" width="300px" />
                        <Button onClick={ () => navigate("/medications")} className="btn-go-home" target="_blank">GO BACK HOME</Button>
                    </Center>
                    <Box className="objects">
                        <Image className="object_rocket" src="https://salehriaz.com/404Page/img/rocket.svg" alt="Rocket"
                               width="120px" position="absolute" top="70%" left="40%" />
                        <Box className="earth-moon">
                            <Image className="object_earth" src="https://salehriaz.com/404Page/img/earth.svg" alt="Earth" width="100px" />
                            <Image className="object_moon" src="https://salehriaz.com/404Page/img/moon.svg" alt="Moon" width="80px" />
                        </Box>
                        <Box className="box_astronaut" animation={`${moveAstronaut} 50s infinite linear both alternate,
                         ${rotateAstronaut} 200s infinite linear both alternate`} position="absolute" top="80%" left="60%"
                             transform="translate(-50%, -50%)">
                            <Image className="object_astronaut" src="https://salehriaz.com/404Page/img/astronaut.svg" alt="Astronaut" width="140px" />
                        </Box>
                    </Box>
                    <Box className="glowing_stars">
                        <GlowingStar top="10%" left="12%" animationDelay="1s" />
                        <GlowingStar top="27%" left="17%" animationDelay="1s" />
                        <GlowingStar top="42%" left="34%" animationDelay="1s" />
                        <GlowingStar top="66%" left="28%" animationDelay="1s" />
                        <GlowingStar top="54%" left="46%" animationDelay="1s" />
                        <GlowingStar top="84%" left="59%" animationDelay="1s" />
                        <GlowingStar top="76%" left="69%" animationDelay="1s" />
                        <GlowingStar top="69%" left="81%" animationDelay="1s" />
                        <GlowingStar top="90%" left="95%" animationDelay="1s" />
                    </Box>
                </Box>
                <Box
                    position="absolute"
                    bottom="5px"
                    right="10px"
                    color="white"
                    fontSize="12px"
                >
                    {/* Liked the page. this guy made it. it is available https://dev.to/stackfindover */}
                    {/* This one is much simpler than the one in the site. the original is much better*/}
                    © 404 Original Page Design by Saleh Riaz Qureshi
                </Box>
            </Box>
        </ChakraProvider>
    );
};

export default NotFoundPage;
