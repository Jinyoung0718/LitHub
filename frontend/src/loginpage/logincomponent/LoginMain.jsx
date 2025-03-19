import { useState } from "react";
import { motion } from "framer-motion";
import LoginForm from "./LoginForm";
import SignupForm from "./SignupForm";
import OAuthLogin from "./OAuthLogin";
import { Container, FormContainer, Header, LinkList } from "./LoginStyles";

const LoginMain = () => {
  const [isSignUp, setIsSignUp] = useState(false);

  const handleToggle = (mode) => {
    setIsSignUp(mode === "signup");
  };

  return (
    <Container>
      <FormContainer>
        <Header>{isSignUp ? "SIGN UP" : "SIGN IN"}</Header>

        <LinkList>
          <button
            onClick={() => handleToggle("signin")}
            className={!isSignUp ? "active" : ""}
          >
            SIGN IN
          </button>
          <button
            onClick={() => handleToggle("signup")}
            className={isSignUp ? "active" : ""}
          >
            SIGN UP
          </button>
        </LinkList>

        <motion.div
          key={isSignUp ? "signup" : "signin"}
          initial={{ opacity: 0, x: isSignUp ? 50 : -50 }}
          animate={{ opacity: 1, x: 0 }}
          exit={{ opacity: 0, x: isSignUp ? -50 : 50 }}
          transition={{ duration: 0.5 }}
        >
          {isSignUp ? <SignupForm /> : <LoginForm />}
        </motion.div>

        <OAuthLogin />
      </FormContainer>
    </Container>
  );
};

export default LoginMain;
