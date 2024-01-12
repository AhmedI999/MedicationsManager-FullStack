import {
    createContext,
    useContext,
} from "react";
import { login as performLogin} from "../../services/client.js";
import {getPatientByEmail as getUser} from "../../services/client.js";
import {encryptData} from "../../services/JwtEncryptionUtil.jsx";
import {errorNotification, successNotification} from "../../services/Notifications.js";
import {deleteCookie, getCookie, setCookie} from "../../services/cookieUtils.js";


const AuthContext = createContext({});
const AuthProvider = ({children}) => {

    const getPatient = async (email) => {
        return new Promise((resolve, reject) => {
            getUser(email)
                .then(res => {
                    resolve(res);
                }).catch(err => {
                reject(err);
            })
        })

    };

    const login = async (emailAndPassword) => {
        try {
            const res = await performLogin(emailAndPassword);
            const jwtToken = res.headers["authorization"];
            const secret = "@nEh;=uPkZt&Xvd(5№6'Ek>M~Rbv~%>j";
            const encryptedToken = await encryptData(jwtToken, secret);
            setCookie("jwt", encryptedToken, 1);

        } catch (err) {
            errorNotification("Login error:", "Authentication Failed");
        }
    };
    const logout = () => {

        deleteCookie("jwt");
        deleteCookie("i");
        if (getCookie("jwt") === null){
            successNotification("Logout", "Logged out successfully")
        } else {
            errorNotification("logout error:", "Failed to logout properly.");
        }
    };

    return (
        <AuthContext.Provider value={{
            login,
            logout,
            getPatient
        }}>
            {children}
        </AuthContext.Provider>
    )
}

export const useAuth = () => useContext(AuthContext);
export default AuthProvider;