import { createRoot } from "react-dom/client";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import AuthProvider from "./common/AuthContext";
import MainPage from "./mainpage/MainPage";
import LoginPage from "./loginpage/LoginPage";
import SocialSignupPage from "./social-signup/SocialSignupPage.jsx";
import MyPage from "./mypage/MyPage.jsx";
import Header from "./common/header/Header";
import Footer from "./common/Footer";
import GlobalStyle from "./styles/GlobalStyle.js";

createRoot(document.getElementById("root")).render(
  <Router>
    <AuthProvider>
      <GlobalStyle />
      <Header />
      <Routes>
        <Route index element={<MainPage />} />
        <Route path="login-register" element={<LoginPage />} />
        <Route path="social-signup" element={<SocialSignupPage />} />
        <Route path="/mypage" element={<MyPage />} />
      </Routes>
      <Footer />
    </AuthProvider>
  </Router>
);
