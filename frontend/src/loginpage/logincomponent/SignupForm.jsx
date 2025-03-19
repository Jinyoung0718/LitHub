import { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import {
  InputBlock,
  EmailInputGroup,
  Button,
  PasswordValidation,
} from "./LoginStyles";

const SignupForm = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    verificationCode: "",
    username: "",
    nickname: "",
    password: "",
    confirmPassword: "",
  });

  const [errorMessage, setErrorMessage] = useState("");
  const [isEmailVerified, setIsEmailVerified] = useState(false);
  const [isCodeSent, setIsCodeSent] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [timer, setTimer] = useState(0);
  const [isNicknameChecked, setIsNicknameChecked] = useState(false);
  const [isCheckingNickname, setIsCheckingNickname] = useState(false);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });

    if (e.target.name === "nickname") {
      setIsNicknameChecked(false);
    }
  };

  const isPasswordMatch =
    formData.password.length > 0 &&
    formData.confirmPassword.length > 0 &&
    formData.password === formData.confirmPassword;

  const handleSendCode = async () => {
    if (isCodeSent || isEmailVerified) return;

    try {
      setErrorMessage("");
      await axios.post("/api/auth/send-email-code", { email: formData.email });

      alert("인증 코드가 발송되었습니다!");
      setIsCodeSent(true);
      setTimer(30);

      const interval = setInterval(() => {
        setTimer((prev) => {
          if (prev === 1) {
            clearInterval(interval);
            setIsCodeSent(false);
          }
          return prev - 1;
        });
      }, 1000);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || "오류 발생");
      setIsCodeSent(false);
    }
  };

  const handleVerifyCode = async () => {
    if (isVerifying || isEmailVerified) return;

    try {
      setErrorMessage("");
      setIsVerifying(true);
      await axios.post("/api/auth/verify-email", {
        email: formData.email,
        code: formData.verificationCode,
      });

      alert("이메일 인증 완료!");
      setIsEmailVerified(true);
      setIsCodeSent(false);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || "오류 발생");
      setFormData({ ...formData, verificationCode: "" });
    } finally {
      setIsVerifying(false);
    }
  };

  const handleCheckNickname = async () => {
    if (isCheckingNickname || isNicknameChecked) return;

    try {
      setErrorMessage("");
      setIsCheckingNickname(true);
      await axios.post("/api/auth/check-nickname", {
        nickName: formData.nickname,
      });

      alert("사용 가능한 닉네임입니다!");
      setIsNicknameChecked(true);
    } catch (error) {
      setErrorMessage(error.response?.data?.message || "닉네임 중복 확인 실패");
    } finally {
      setIsCheckingNickname(false);
    }
  };

  const handleSignup = async () => {
    if (!isPasswordMatch || !isEmailVerified || !isNicknameChecked) return;

    try {
      setErrorMessage("");
      await axios.post("/api/auth/signup", {
        userEmail: formData.email,
        userName: formData.username,
        nickName: formData.nickname,
        userPassword: formData.password,
      });

      alert("회원가입 성공!");
      navigate("/");
    } catch (error) {
      setErrorMessage(error.response?.data?.message || "오류 발생");
    }
  };

  return (
    <form onSubmit={(e) => e.preventDefault()}>
      {errorMessage && <p style={{ color: "red" }}>{errorMessage}</p>}

      <EmailInputGroup>
        <input
          type="email"
          name="email"
          placeholder="사용자 이메일"
          value={formData.email}
          onChange={handleChange}
          disabled={isEmailVerified}
        />
        <button
          type="button"
          onClick={handleSendCode}
          disabled={isCodeSent || isEmailVerified}
        >
          {isEmailVerified
            ? "Verified"
            : isCodeSent
            ? `재시도 (${timer}s)`
            : "Send Code"}
        </button>
      </EmailInputGroup>

      <EmailInputGroup>
        <input
          type="text"
          name="verificationCode"
          placeholder="이메일 인증번호"
          value={formData.verificationCode}
          onChange={handleChange}
          disabled={isEmailVerified}
        />
        <button
          type="button"
          onClick={handleVerifyCode}
          disabled={isVerifying || isEmailVerified}
        >
          {isEmailVerified ? "Verified" : "Verify"}
        </button>
      </EmailInputGroup>

      <EmailInputGroup>
        <input
          type="text"
          name="nickname"
          placeholder="사용자 닉네임"
          value={formData.nickname}
          onChange={handleChange}
        />
        <button
          type="button"
          onClick={handleCheckNickname}
          disabled={isCheckingNickname || isNicknameChecked}
        >
          {isNicknameChecked ? "Confirmed" : "Check"}
        </button>
      </EmailInputGroup>

      <InputBlock>
        <input
          type="password"
          name="password"
          placeholder="비밀번호"
          value={formData.password}
          onChange={handleChange}
        />
      </InputBlock>

      <InputBlock>
        <input
          type="password"
          name="confirmPassword"
          placeholder="비밀번호 확인"
          value={formData.confirmPassword}
          onChange={handleChange}
        />
        {formData.confirmPassword.length > 0 && (
          <PasswordValidation isMatch={isPasswordMatch}>
            {isPasswordMatch
              ? "비밀번호가 일치합니다"
              : "비밀번호가 일치하지 않습니다"}
          </PasswordValidation>
        )}
      </InputBlock>

      <Button
        type="button"
        onClick={handleSignup}
        disabled={!isPasswordMatch || !isEmailVerified || !isNicknameChecked}
      >
        Sign Up
      </Button>
    </form>
  );
};

export default SignupForm;
