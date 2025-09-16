import React, { createContext, useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";

export const AuthContext = createContext();

const AuthProvider = ({ children }) => {
  const [accessToken, setAccessToken] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const location = useLocation();

  useEffect(() => {
    axios.defaults.baseURL = "http://localhost:8080";
    axios.defaults.withCredentials = true;
    axios.defaults.headers.common["Content-Type"] = "application/json";

    const excludedPaths = ["/login-register", "/social-signup"];
    if (!excludedPaths.includes(location.pathname)) {
      checkLoginStatus();
    } else {
      setIsLoading(false);
    }
  }, [location.pathname]);

  const checkLoginStatus = async () => {
    try {
      const response = await axios.get("/api/info/check");
      if (response.data.success) {
        setAccessToken("authenticated");
      } else {
        setAccessToken(null);
      }
    } catch (error) {
      console.log(error.response);
      setAccessToken(null);
    } finally {
      setIsLoading(false);
    }
  };

  const saveAccessToken = (token) => {
    setAccessToken(token);
  };

  const logout = async (navigateCallback) => {
    try {
      await axios.post("/api/auth/logout", null, { withCredentials: true });
      setAccessToken(null);

      if (navigateCallback) navigateCallback("/login-register");
    } catch (error) {
      setAccessToken(null);
    }
  };

  return (
    <AuthContext.Provider
      value={{ accessToken, saveAccessToken, logout, isLoading }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export default AuthProvider;
