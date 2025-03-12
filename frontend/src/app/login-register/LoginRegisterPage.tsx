"use client";

import { useState } from "react";
import { motion } from "framer-motion";
import {
  Container,
  FormContainer,
  Header,
  LinkList,
  Separator,
  SocialIcons,
  SocialIcon,
} from "./LoginRegister.styles";
import SignupForm from "./SignupForm";
import LoginForm from "./LoginForm";
import Image from "next/image";

export default function LoginRegisterPage() {
  const [isSignUp, setIsSignUp] = useState(false);

  const handleToggle = (mode: string) => {
    setIsSignUp(mode === "signup");
  };

  return (
    <Container>
      <FormContainer
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5, ease: "easeOut" }}
      >
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

        <Separator>OR</Separator>

        <SocialIcons>
          <SocialIcon as="button">
            <Image
              src="/google-icon.png"
              alt="Google Icon"
              width={32}
              height={32}
            />
          </SocialIcon>
          <SocialIcon as="button">
            <Image
              src="/naver-icon.png"
              alt="Naver Icon"
              width={32}
              height={32}
            />
          </SocialIcon>
        </SocialIcons>
      </FormContainer>
    </Container>
  );
}
