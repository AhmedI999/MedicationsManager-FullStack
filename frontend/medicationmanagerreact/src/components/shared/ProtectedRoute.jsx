import ErrorPage from "./ErrorPage.jsx";
import {isCookieValid} from "../../services/jwt/cookieUtils.js";

const isUserAuthenticated = () => {
    return isCookieValid('jwt');
};

const ProtectedRoute = ({children}) => {
    return isUserAuthenticated() ? children : <ErrorPage />;
};
export default ProtectedRoute;